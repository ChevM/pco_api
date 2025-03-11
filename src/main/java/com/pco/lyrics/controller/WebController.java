package com.pco.lyrics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving web pages.
 */
@Controller
public class WebController {

    /**
     * Serve the main page.
     *
     * @return The name of the view to render
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
} 