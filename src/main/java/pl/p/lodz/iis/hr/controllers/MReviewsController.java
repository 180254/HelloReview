package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;
import pl.p.lodz.iis.hr.models.courses.Course;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.repositories.CourseRepository;
import pl.p.lodz.iis.hr.repositories.ParticipantRepository;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;

import java.util.Collections;
import java.util.List;

@Controller
class MReviewsController {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ParticipantRepository participantRepository;

    @RequestMapping(
            value = "/m/reviews",
            method = RequestMethod.GET)
    @Transactional
    public String rList(Model model) {

        List<Review> reviews = reviewRepository.findAll();
        model.addAttribute("reviews", reviews);
        model.addAttribute("newButton", true);

        return "m-reviews";
    }

    @RequestMapping(
            value = "/m/reviews/{reviewID}",
            method = RequestMethod.GET)
    @Transactional
    public String rListOne(@PathVariable long reviewID,
                           Model model) {

        if (!reviewRepository.exists(reviewID)) {
            throw new ResourceNotFoundException();
        }

        Review review = reviewRepository.findOne(reviewID);
        model.addAttribute("reviews", Collections.singletonList(review));
        model.addAttribute("newButton", false);

        return "m-reviews";
    }

    @RequestMapping(
            value = "/m/reviews/for/course/{courseID}",
            method = RequestMethod.GET)
    @Transactional
    public String rListForCourse(@PathVariable long courseID,
                                 Model model) {

        if (!courseRepository.exists(courseID)) {
            throw new ResourceNotFoundException();
        }

        Course course = courseRepository.findOne(courseID);

        model.addAttribute("reviews", course.getReviews());
        model.addAttribute("newButton", false);

        return "m-reviews";
    }
}
