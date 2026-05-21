package com.inf.medical_record_system.web.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeViewController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "index";
    }
}