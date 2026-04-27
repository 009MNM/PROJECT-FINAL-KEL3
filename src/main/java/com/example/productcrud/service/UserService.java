package com.example.productcrud.service;

import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Pakai BCrypt agar password di database aman (terenkripsi)
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void updatePassword(String email, String newPassword) {
        // Cari user berdasarkan email yang diinput
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Encode password baru
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);

            // Simpan perubahan
            userRepository.save(user);
        } else {
            // Lempar error jika email tidak ada di DB
            throw new RuntimeException("Email tidak terdaftar dalam sistem!");
        }
    }
}
