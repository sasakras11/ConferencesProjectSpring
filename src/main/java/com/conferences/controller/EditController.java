package com.conferences.controller;

import com.conferences.entity.Speech;
import com.conferences.entity.User;
import com.conferences.service.ConferenceService;
import com.conferences.service.RatingService;
import com.conferences.service.SpeechService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import java.util.Optional;


@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EditController extends AbstractController{


    private final UserBean userBean;
    private static final Logger LOGGER = LoggerFactory.getLogger(EditController.class);
    private final ConferenceService conferenceService;
    private final SpeechService speechService;
    private final RatingService ratingService;


    @GetMapping(value = {"/editConferencePage"})
    public ModelAndView editConferencePage(@RequestParam("id") String id) {

        User user = userBean.getUser();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("conference", conferenceService.findById(id));
        modelAndView.setViewName(user.getRole().name().toLowerCase() + "/conferenceEdit");

        return modelAndView;
    }


    @RequestMapping(value = {"/edit"})
    //TODO move logic to service and if exception happened move back to conference list and log
    public ModelAndView editConference(@RequestParam(value = "date") String date,
                                       @RequestParam(value = "name") String name, @RequestParam("conferenceId") String id) {

        ModelAndView modelAndView = new ModelAndView();
        User user = userBean.getUser();
        conferenceService.editConference(name, date, id);
        modelAndView.addObject("conferences", conferenceService.findComingConferences("1"));
        modelAndView.setViewName(user.getRole().name().toLowerCase() + "/conferences");

        return modelAndView;
    }

    @RequestMapping(value = {"/editSpeechPage"})
    public ModelAndView editSpeechPage(@RequestParam("speechId") String speechId) {

        ModelAndView modelAndView = new ModelAndView();
        User user = userBean.getUser();
        Speech speech = speechService.findById(speechId);
        modelAndView.addObject("speech", speech);
        modelAndView.addObject("pageNum", 1);
        modelAndView.setViewName(user.getRole().name().toLowerCase() + "/speechEdit");
        return modelAndView;

    }

    @RequestMapping(value = "/editSpeech") //TODO move logic to service
    public ModelAndView editSpeech(@RequestParam(value = "topic", required = false) Optional<String> topic,
                                   @RequestParam(value = "startHour", required = false) Optional<String> startHour,
                                   @RequestParam(value = "endHour", required = false) Optional<String> endHour,
                                   @RequestParam("id") String id,
                                   @RequestParam("suggestedTopic") String suggestedTopic) {

        ModelAndView modelAndView = new ModelAndView();


        Speech speech = speechService.findById(id);
        speech.setSuggestedTopic(suggestedTopic);
        topic.ifPresent(speech::setTopic);
        startHour.ifPresent(x -> speech.setStartHour(Integer.parseInt(startHour.get())));
        endHour.ifPresent(x -> speech.setEndHour(Integer.parseInt(endHour.get())));
        speech.setSuggestedTopic(suggestedTopic);
        speechService.save(speech);
        User user = userBean.getUser();
        modelAndView.addObject("speeches", speechService.findAllByConference(speech.getConference().getConferenceId()));
        modelAndView.addObject("userSpeeches", speechService.findAllByUserId(user.getUserId()));

        modelAndView.setViewName(user.getRole().name().toLowerCase() + "/speeches");
        return modelAndView;


    }

    @RequestMapping(value = {"/ratingPage"})
    public ModelAndView ratingPage() {

        ModelAndView modelAndView = new ModelAndView();
        User user = userBean.getUser();
        modelAndView.addObject("rating", ratingService.findAll());
        modelAndView.setViewName(user.getRole().name().toLowerCase() + "/rating");

        return modelAndView;
    }

    @RequestMapping(value = {"/editRating"})
    public ModelAndView editRating(@RequestParam("ratingId") String ratingId, @RequestParam("ratingMark") String ratingMark) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("rating", ratingService.findAll());
        User user = userBean.getUser();
        ratingService.changeSpeakerRating(ratingId, ratingMark);
        modelAndView.setViewName(user.getRole().name().toLowerCase() + "/rating");

        return modelAndView;

    }
}
