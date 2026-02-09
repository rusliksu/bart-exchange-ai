package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class WebController {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OfferRepository offerRepository;
    private final ExchangeRepository exchangeRepository;
    private final MessageRepository messageRepository;
    private final ReviewRepository reviewRepository;
    private final GroupRepository groupRepository;
    
    @GetMapping("/web")
    public String home(Model model) {
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("categoryCount", categoryRepository.count());
        model.addAttribute("offerCount", offerRepository.count());
        model.addAttribute("exchangeCount", exchangeRepository.count());
        model.addAttribute("messageCount", messageRepository.count());
        model.addAttribute("reviewCount", reviewRepository.count());
        model.addAttribute("groupCount", groupRepository.count());
        return "home";
    }
    
    @GetMapping("/web/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }
    
    @GetMapping("/web/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "categories";
    }
    
    @GetMapping("/web/offers")
    public String offers(Model model) {
        model.addAttribute("offers", offerRepository.findAll());
        return "offers";
    }
    
    @GetMapping("/web/exchanges")
    public String exchanges(Model model) {
        model.addAttribute("exchanges", exchangeRepository.findAll());
        return "exchanges";
    }
    
    @GetMapping("/web/messages")
    public String messages(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "messages";
    }
    
    @GetMapping("/web/reviews")
    public String reviews(Model model) {
        model.addAttribute("reviews", reviewRepository.findAll());
        return "reviews";
    }
    
    @GetMapping("/web/groups")
    public String groups(Model model) {
        model.addAttribute("groups", groupRepository.findAll());
        return "groups";
    }
    
    @GetMapping("/web/api-docs")
    public String apiDocs() {
        return "api-docs";
    }
}