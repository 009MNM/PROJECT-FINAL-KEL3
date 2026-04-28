package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.User;
import com.example.productcrud.service.CategoryService;
import com.example.productcrud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller // WAJIB: Pakai @Controller agar bisa return halaman .html
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserRepository userRepository;

    // Helper untuk ambil User yang sedang login
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 1. LIST CATEGORY (Hanya milik user tersebut)
    @GetMapping
    public String listCategories(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getCurrentUser(userDetails);
        model.addAttribute("categories", categoryService.findByUser(user));
        return "category/list";
    }

    // 2. SHOW FORM TAMBAH
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/form";
    }

    // 3. SAVE ATAU UPDATE
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("category") Category category,
                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        category.setUser(user); // Set userId sebagai Foreign Key (Syarat Tugas)
        categoryService.save(category);
        return "redirect:/categories";
    }

        // 4. SHOW FORM EDIT
        @GetMapping("/edit/{id}")
        public String showEditForm(@PathVariable Long id, Model model) {
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
            model.addAttribute("category", category);
            return "category/form";
        }

    // 5. DELETE CATEGORY
    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/categories";
    }
}