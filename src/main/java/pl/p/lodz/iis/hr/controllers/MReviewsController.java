package pl.p.lodz.iis.hr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.p.lodz.iis.hr.models.courses.Review;
import pl.p.lodz.iis.hr.repositories.ReviewRepository;

import java.util.List;

@Controller
class MReviewsController {

    @Autowired private ReviewRepository reviewRepository;

    @RequestMapping(
            value = "/m/reviews",
            method = RequestMethod.GET)
    public String rList(Model model) {

        List<Review> reviews = reviewRepository.findAll();
        model.addAttribute("reviews", reviews);

        return "m-reviews";
    }
}
