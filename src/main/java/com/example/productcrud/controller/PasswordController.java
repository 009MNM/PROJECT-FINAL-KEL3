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

    // --- FITUR LUPA PASSWORD (AKSES LUAR/BELUM LOGIN) ---

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("success", true);
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "reset-password";
    }

    // --- FITUR GANTI PASSWORD (AKSES DALAM/MENU "HALO CINTAAA") ---

    @GetMapping("/change-password")
    public String showChangePasswordForm(Principal principal, Model model) {
        // Ambil email/username otomatis dari user yang sedang login
        String email = principal.getName();
        model.addAttribute("email", email);
        return "change-password";
    }

    // --- PROSES UPDATE (DIPAKAI KEDUA FITUR DI ATAS) ---

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("email") String email,
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Konfirmasi password tidak cocok!");
            model.addAttribute("email", email);
            // Jika ada error, kembalikan ke halaman yang sesuai
            return "reset-password";
        }

        try {
            userService.updatePassword(email, newPassword);
            // Setelah berhasil, lempar ke logout/login biar user masuk pake password baru
            return "redirect:/login?logout";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "reset-password";
        }
    }
}