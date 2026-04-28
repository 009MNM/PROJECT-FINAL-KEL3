package com.example.productcrud.controller;

import com.example.productcrud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class PasswordController {

    @Autowired
    private UserService userService;

    // --- TAMPILKAN FORM (GUEST ATAU LOGIN) ---
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Principal principal, Model model) {
        // Cek dulu: apakah user sudah login?
        // Jika sudah, kita bantu isi field email-nya secara otomatis.
        // Jika belum (principal == null), biarkan email kosong.
        if (principal != null) {
            model.addAttribute("email", principal.getName());
        }
        return "forgot-password";
    }

    // --- PROSES CEK EMAIL (DARI FORM) ---
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        // Logika sederhana: langsung anggap sukses atau tambahkan pengecekan ke DB jika perlu
        model.addAttribute("email", email);
        model.addAttribute("success", true);
        return "forgot-password";
    }

    // --- TAMPILKAN FORM INPUT PASSWORD BARU ---
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "reset-password";
    }

    // --- PROSES UPDATE PASSWORD KE DATABASE ---
    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("email") String email,
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Konfirmasi password tidak cocok!");
            model.addAttribute("email", email);
            return "reset-password";
        }

        try {
            userService.updatePassword(email, newPassword);
            // Redirect ke login dan tambahkan pesan sukses via parameter
            return "redirect:/login?resetSuccess";
        } catch (Exception e) {
            model.addAttribute("error", "Gagal update password: " + e.getMessage());
            model.addAttribute("email", email);
            return "reset-password";
        }
    }
}