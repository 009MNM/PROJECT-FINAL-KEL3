package com.example.productcrud.controller;

import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. Menampilkan Halaman Profil
    @GetMapping
    public String viewProfile(Principal principal, Model model) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "profile/view";
    }

    // 2. Menampilkan Form Edit Profil
    @GetMapping("/edit")
    public String editProfileForm(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile/edit";
    }

    // 3. Proses Simpan Update Profil
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") User userUpdates, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update field sesuai input
        user.setFullName(userUpdates.getFullName());
        user.setEmail(userUpdates.getEmail());
        user.setPhoneNumber(userUpdates.getPhoneNumber());
        user.setAddress(userUpdates.getAddress());
        user.setBio(userUpdates.getBio());
        user.setProfileImageUrl(userUpdates.getProfileImageUrl());

        userRepository.save(user);
        return "redirect:/profile?success";
    }
}