package pl.p.lodz.iis.hr.services;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.appconfig.AppConfig;
import pl.p.lodz.iis.hr.exceptions.GHCommunicationException;
import pl.p.lodz.iis.hr.exceptions.InternalException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.models.courses.*;
import pl.p.lodz.iis.hr.models.forms.Form;
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.repositories.FormRepository;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;
import pl.p.lodz.iis.hr.utils.GHExecutor;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GHReviewCreator {

    private final ReviewRepository reviewRepository;
    private final CommissionRepository commissionRepository;
    private final CourseRepository courseRepository;
    private final FormRepository formRepository;
    private final AppConfig appConfig;
    private final GitHub gitHubFail;
    private final GHTaskScheduler ghTaskScheduler;

    @Autowired
    GHReviewCreator(ReviewRepository reviewRepository,
                    CommissionRepository commissionRepository,
                    CourseRepository courseRepository,
                    FormRepository formRepository,
                    AppConfig appConfig,
                    @Qualifier("ghFail") GitHub gitHubFail,
                    GHTaskScheduler ghTaskScheduler) {
        this.reviewRepository = reviewRepository;
        this.commissionRepository = commissionRepository;
        this.courseRepository = courseRepository;
        this.formRepository = formRepository;
        this.appConfig = appConfig;
        this.gitHubFail = gitHubFail;
        this.ghTaskScheduler = ghTaskScheduler;
    }

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

    public GHRepository getRepositoryByName(String repositoryFullName) throws GHCommunicationException {
        return GHExecutor.ex(() -> gitHubFail.getRepository(repositoryFullName));
    }

    public List<String> createReview(GHReviewForm ghReviewForm,
                                     GHRepository ghRepository,
                                     HttpServletResponse response)
            throws LocalizableErrorRestException {

        try {
            Course course = courseRepository.getOne(ghReviewForm.getCourseID());
            List<Participant> participants = course.getParticipants();
            Form form = formRepository.getOne(ghReviewForm.getFormID());
            List<GHRepository> forks = GHExecutor.ex(() -> ghRepository.listForks().asList());

            Map<String, GHRepository> forksMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            forks.forEach(fork -> forksMap.put(fork.getOwnerName(), fork));

            List<Participant> partiWhoForked = participants.stream()
                    .filter(p -> forksMap.containsKey(p.getGitHubName()))
                    .collect(Collectors.toList());

            List<Participant> partiWhoNotForked = participants.stream()
                    .filter(p -> !forksMap.containsKey(p.getGitHubName()))
                    .collect(Collectors.toList());

            long respPerPeerVsForked = Math.min((long) partiWhoForked.size() - 1L, ghReviewForm.getRespPerPeer());
            long respPerPeer2 = Math.max(respPerPeerVsForked, 0L);

            boolean preconditionFailed = (!partiWhoNotForked.isEmpty()
                    || (ghReviewForm.getRespPerPeer() != respPerPeer2));
            if ((ghReviewForm.getIgnoreWarning() == 0L) /*&& preconditionFailed*/) {
                response.setStatus(HttpStatus.PRECONDITION_FAILED.value());

                List<String> warning = new ArrayList<>(10);
                warning.add(String.valueOf(respPerPeer2));
                warning.add(String.valueOf(partiWhoNotForked.size()));
                warning.add(String.valueOf(participants.size()));
                partiWhoNotForked.forEach((p) -> warning.add(p.getName()));
                return warning;
            }

            List<Participant> mulParticipants = new ArrayList<>(10);
            long expectedMulParSize = (participants.size() * respPerPeer2);
            while (mulParticipants.size() < expectedMulParSize) {
                mulParticipants.addAll(partiWhoForked);
            }
            Collections.shuffle(mulParticipants);
            mulParticipants.addAll(partiWhoForked); // to be sure, that is enough

            Review review = new Review(
                    ghReviewForm.getName(), respPerPeer2, course, form, ghReviewForm.getRepositoryFullName()
            );
            Collection<Commission> responses = new ArrayList<>(10);

            for (Participant participant : partiWhoNotForked) {
                Commission rResponse = new Commission(review, participant, null, (String) null);
                rResponse.setStatus(CommissionStatus.NOT_FORKED);
                responses.add(rResponse);
            }

            Collection<Participant> assessedCollection = new LinkedList<>();

            for (Participant assessor : course.getParticipants()) {
                assessedCollection.clear();

                for (long lo = 0L; lo < respPerPeer2; lo++) {

                    Participant assessed = popUnique(mulParticipants, assessor, assessedCollection);
                    assessedCollection.add(assessed);

                    GHRepository assessedRepo = forksMap.get(assessed.getGitHubName());

                    Commission rResponse = new Commission(review, assessed, assessor, assessedRepo.getHtmlUrl());
                    responses.add(rResponse);

                }
            }

            reviewRepository.save(review);
            commissionRepository.save(responses);

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
        throw new InternalException();
    }
}
