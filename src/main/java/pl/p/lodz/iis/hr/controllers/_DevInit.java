package pl.p.lodz.iis.hr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.exceptions.LocalizedErrorRestException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Participant;
import pl.p.lodz.iis.hr.services.RepositoryProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//@Controller
public class _DevInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(_DevInit.class);

    private final RepositoryProvider repositoryProvider;
    private final MFormsController mFormsController;

    @Autowired
    public _DevInit(RepositoryProvider repositoryProvider,
                    MFormsController mFormsController) {
        this.repositoryProvider = repositoryProvider;
        this.mFormsController = mFormsController;
    }

    @RequestMapping(
            value = "/reset",
            method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public String reset() {
        
        repositoryProvider.course().deleteAll();
        repositoryProvider.form().deleteAll();

        return "OK";
    }

    @RequestMapping(
            value = "/init",
            method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public String init()
            throws IOException, ErrorPageException,
            LocalizableErrorRestException, LocalizedErrorRestException {

        repositoryProvider.course().deleteAll();
        repositoryProvider.form().deleteAll();

        repositoryProvider.course().flush();
        repositoryProvider.form().flush();

        Course course = new Course("MTO 2014/2015");
        repositoryProvider.course().save(course);

        List<String> partiList = Arrays.asList(
                "180254", "astepniewski", "bliheris", "damianspinek", "Dastipio",
                "dzemba", "gitkapicel", "godziatkowski", "GorskiJakub", "grzego699",
                "grzego69", "Hirikiawashi", "jarkos", "JMichalak", "180132",
                "kaminskaarleta", "kj179602", "markol", "matihuf", "michalberenc",
                "mirydz", "sewemark", "smoczynskaj", "wozniakk");

        for (String parti : partiList) {
            Participant participant = new Participant(course, parti, parti);
            repositoryProvider.participant().save(participant);
        }

        Course course1 = new Course("Course1");
        repositoryProvider.course().save(course1);

        Participant participant1 = new Participant(course1, "180254", "180254");
        repositoryProvider.participant().save(participant1);

        Participant participant2 = new Participant(course1, "astepniewski", "astepniewski");
        repositoryProvider.participant().save(participant2);

        mFormsController.kAddPOST("Form1", mFormsController.xmlExample(), "add");

        return "OK";
    }
}
