package pl.p.lodz.iis.hr.services;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.appconfig.AppConfig;
import pl.p.lodz.iis.hr.exceptions.GHCommunicationException;
import pl.p.lodz.iis.hr.exceptions.InternalServerErrorException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.models.courses.*;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.utils.GHExecutor;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Util class for GET/POST /m/reviews/add.<br/>
 * Includes whole logic of review creation.
 */
@Service
public class GHReviewCreator {

    private final RepositoryProvider repositoryProvider;
    private final AppConfig appConfig;
    private final GitHub gitHubFail;
    private final GHTaskScheduler ghTaskScheduler;

    @Autowired
    GHReviewCreator(RepositoryProvider repositoryProvider,
                    AppConfig appConfig,
                    @Qualifier("ghFail") GitHub gitHubFail,
                    GHTaskScheduler ghTaskScheduler) {
        this.repositoryProvider = repositoryProvider;
        this.appConfig = appConfig;
        this.gitHubFail = gitHubFail;
        this.ghTaskScheduler = ghTaskScheduler;
    }

    /**
     * @return List of course repo full names (ex. mathew/lab_1).
     * @throws GHCommunicationException
     */
    public List<String> getListOfCourseRepos() throws GHCommunicationException {
        List<String> repoList = new ArrayList<>(10);

        GHExecutor.ex(() -> {
            for (String username : appConfig.getGitHubConfig().getCourseRepos().getUserNames()) {
                gitHubFail.getUser(username).listRepositories()
                        .asList().stream()
                        .map(ghRepo -> String.format("%s/%s", ghRepo.getOwnerName(), ghRepo.getName()))
                        .forEach(repoList::add);
            }
        });

        return repoList;
    }

    /**
     * Convert repository full name to GHRepository object.
     *
     * @param repositoryFullName repo full name  (ex. mathew/lab_1)
     * @return org.kohsuke.github.GHRepository
     * @throws GHCommunicationException
     */
    public GHRepository getRepositoryByName(String repositoryFullName) throws GHCommunicationException {
        return GHExecutor.ex(() -> gitHubFail.getRepository(repositoryFullName));
    }

    /**
     * Create review.
     *
     * @param ghReviewAddForm GHReviewAddForm ModelAttribute, must be already validated
     * @param ghRepository    reviewed repository
     * @param response        HttpServletResponse from mapping
     * @return singleton list with id number of created review
     * @throws LocalizableErrorRestException if any processing error occured
     */
    public List<String> createReview(GHReviewAddForm ghReviewAddForm,
                                     GHRepository ghRepository,
                                     HttpServletResponse response)
            throws LocalizableErrorRestException {

        try {
            // get course, list of participants in course, and used form in review
            Course course = repositoryProvider.course().getOne(ghReviewAddForm.getCourseIDLong());
            List<Participant> participants = course.getParticipants();
            Form form = repositoryProvider.form().getOne(ghReviewAddForm.getFormIDLong());

            // get map of forks (GitHub username -> GHRepository)
            List<GHRepository> forks = GHExecutor.ex(() -> ghRepository.listForks().asList());
            Map<String, GHRepository> forksMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            forks.forEach(fork -> forksMap.put(fork.getOwnerName(), fork));

            // get list of participants who forked reviewed project
            List<Participant> partiWhoForked = participants.stream()
                    .filter(p -> forksMap.containsKey(p.getGitHubName()))
                    .collect(Collectors.toList());

            // get list of participants who not forked reviewed project
            List<Participant> partiWhoNotForked = participants.stream()
                    .filter(p -> !forksMap.containsKey(p.getGitHubName()))
                    .collect(Collectors.toList());

            // calculate effective number of "responses per peer" - check if given number is proper
            // responses per peer is number of review task for each peer
            long respPerPeerVsForks = Math.min((long) partiWhoForked.size() - 1L, ghReviewAddForm.getRespPerPeerLong());
            long respPerPeer2 = Math.max(respPerPeerVsForks, 0L);

            // if it is first step of review creation do not create review
            // first return info what will be really done
            // if information was confirmed continue
            boolean preconditionFailed = (!partiWhoNotForked.isEmpty()
                    || (ghReviewAddForm.getRespPerPeerLong() != respPerPeer2));
            if ((ghReviewAddForm.getIgnoreWarning() == 0L) /*&& preconditionFailed*/) {
                response.setStatus(HttpStatus.PRECONDITION_FAILED.value());

                List<String> warning = new ArrayList<>(10);
                warning.add(String.valueOf(respPerPeer2));
                warning.add(String.valueOf(partiWhoNotForked.size()));
                warning.add(String.valueOf(participants.size()));
                partiWhoNotForked.forEach((p) -> warning.add(p.getName()));
                return warning;
            }

            // get list of "assessed" participants
            // it is list of participants who forked project, multiplied by number of "responses per peer"
            // and shuffled, as every participant should got random review task (commission)
            List<Participant> mulParticipants = new ArrayList<>(10);
            long expectedMulParSize = (participants.size() * respPerPeer2);
            while (mulParticipants.size() < expectedMulParSize) {
                mulParticipants.addAll(partiWhoForked);
            }
            Collections.shuffle(mulParticipants);
            mulParticipants.addAll(partiWhoForked); // to be sure, that is enough

            // create review model
            Review review = new Review(
                    ghReviewAddForm.getName(), respPerPeer2, course, form, ghReviewAddForm.getRepositoryFullName()
            );
            Collection<Commission> responses = new ArrayList<>(10);

            // add to review model info about participants who not forked project
            for (Participant participant : partiWhoNotForked) {
                Commission rResponse = new Commission(review, participant, null, (String) null);
                rResponse.setStatus(CommissionStatus.NOT_FORKED);
                responses.add(rResponse);
            }

            // list of already assessed participants,
            // to be ensure that one assessor will not have two same review task (commission)
            Collection<Participant> assessedCollection = new LinkedList<>();

            // add commissions for each participant
            for (Participant assessor : course.getParticipants()) {
                assessedCollection.clear();

                // each participant got "respPerPeer2" number of commissions
                for (long lo = 0L; lo < respPerPeer2; lo++) {

                    Participant assessed = popUnique(mulParticipants, assessor, assessedCollection);
                    assessedCollection.add(assessed);

                    GHRepository assessedRepo = forksMap.get(assessed.getGitHubName());

                    Commission rResponse = new Commission(review, assessed, assessor, assessedRepo.getHtmlUrl());
                    responses.add(rResponse);

                }
            }

            // finally save review to database
            repositoryProvider.review().save(review);
            repositoryProvider.commission().save(responses);

            // and register github clone task, to provide anonymous copy
            responses.stream()
                    .filter(r -> r.getStatus() != CommissionStatus.NOT_FORKED)
                    .forEach(ghTaskScheduler::registerClone);

            return Collections.singletonList(String.valueOf(review.getId()));

        } catch (GHCommunicationException e) {
            throw (LocalizableErrorRestException)
                    new LocalizableErrorRestException("NoGitHub", e.toString()).initCause(e);
        }
    }

    private <T> T popUnique(Collection<T> collection, T meExclude, Collection<T> excludeCollection) {
        for (T collElement : collection) {

            if (!meExclude.equals(collElement) && !excludeCollection.contains(collElement)) {
                collection.remove(collElement);
                return collElement;
            }

        }

        throw new InternalServerErrorException();
    }
}
