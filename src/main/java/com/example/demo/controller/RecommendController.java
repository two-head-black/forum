package com.example.demo.controller;

import com.example.demo.dto.PaginationDTO;
import com.example.demo.dto.RecommendationDTO;
import com.example.demo.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RecommendController {
    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/recommend")
    public String recommend(Model model,
                            @RequestParam(name="page", defaultValue = "1") Integer page,
                            @RequestParam(name="size", defaultValue = "5") Integer size) {
        PaginationDTO pagination = recommendationService.list(page, size);
        model.addAttribute("pagination",pagination);
        return "recommend";
    }

}