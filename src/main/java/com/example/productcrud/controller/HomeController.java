package com.example.productcrud.controller;

import com.example.productcrud.model.User;
import com.example.productcrud.model.Product;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.ProductService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ProductService productService;
    private final UserRepository userRepository;

    public HomeController(ProductService productService, UserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        List<Product> products = productService.findAllByOwner(currentUser);

        // Statistik dasar
        long totalProduk = products.size();
        long produkAktif = products.stream().filter(Product::isActive).count();
        long produkTidakAktif = totalProduk - produkAktif;

        // Total nilai inventory
        long totalNilai = products.stream()
                .mapToLong(p -> p.getPrice() * p.getStock())
                .sum();

        // Jumlah per kategori
        Map<String, Long> perKategori = products.stream()
                .filter(p -> p.getCategory() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getCategory().getName(),
                        Collectors.counting()
                ));

        // Low stock (stok < 5)
        List<Product> lowStock = products.stream()
                .filter(p -> p.getStock() < 5)
                .collect(Collectors.toList());

        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("totalProduk", totalProduk);
        model.addAttribute("produkAktif", produkAktif);
        model.addAttribute("produkTidakAktif", produkTidakAktif);
        model.addAttribute("totalNilai", totalNilai);
        model.addAttribute("perKategori", perKategori);
        model.addAttribute("lowStock", lowStock);

        return "home";
    }
}