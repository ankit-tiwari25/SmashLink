package com.springproject.SmashLink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title", "SmashLink thymleaf");
        return  "index";
    }

    @GetMapping("/about")
    public String about(){
        return  "about";
    }

}
