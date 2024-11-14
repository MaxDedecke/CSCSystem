package com.css.one.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class OnboardingController {

    @GetMapping("/onboarding")
    public ModelAndView onboarding(@RequestParam("token") String token) {
        
        if(token != null) {        	
        	return new ModelAndView("redirect:/onboarding/" + token);
        } else {
            return new ModelAndView("login", HttpStatus.UNAUTHORIZED);
        }
    }
}