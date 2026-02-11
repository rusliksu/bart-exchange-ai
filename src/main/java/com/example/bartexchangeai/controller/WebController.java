package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebController {

    private static final Pageable DEFAULT_PAGE = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "id"));

    private final DashboardController dashboardController;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OfferRepository offerRepository;
    private final ExchangeRepository exchangeRepository;
    private final MessageRepository messageRepository;
    private final ReviewRepository reviewRepository;
    private final GroupRepository groupRepository;

    @GetMapping("/web")
    public String home(Model model) {
        model.addAttribute("stats", dashboardController.getStats());
        return "home";
    }

    @GetMapping("/web/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll(DEFAULT_PAGE).getContent());
        return "users";
    }

    @GetMapping("/web/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll(DEFAULT_PAGE).getContent());
        return "categories";
    }

    @GetMapping("/web/offers")
    public String offers(Model model) {
        model.addAttribute("offers", offerRepository.findAll(DEFAULT_PAGE).getContent());
        return "offers";
    }

    @GetMapping("/web/exchanges")
    public String exchanges(Model model) {
        model.addAttribute("exchanges", exchangeRepository.findAll(DEFAULT_PAGE).getContent());
        return "exchanges";
    }

    @GetMapping("/web/messages")
    public String messages(Model model) {
        model.addAttribute("messages", messageRepository.findAll(DEFAULT_PAGE).getContent());
        return "messages";
    }

    @GetMapping("/web/reviews")
    public String reviews(Model model) {
        model.addAttribute("reviews", reviewRepository.findAll(DEFAULT_PAGE).getContent());
        return "reviews";
    }

    @GetMapping("/web/groups")
    public String groups(Model model) {
        model.addAttribute("groups", groupRepository.findAll(DEFAULT_PAGE).getContent());
        return "groups";
    }

    @GetMapping("/web/api-docs")
    public String apiDocs() {
        return "api-docs";
    }
}