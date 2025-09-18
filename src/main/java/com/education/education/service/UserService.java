package com.education.education.service;

import com.education.education.entity.User;
import com.education.education.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createOrGetUser(String phone, String name, String classLevel, String district, String language,
            String password) {
        Optional<User> existingUser = userRepository.findByPhone(phone);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update user details if provided
            if (name != null && !name.isEmpty()) {
                user.setName(name);
            }
            if (classLevel != null && !classLevel.isEmpty()) {
                user.setClassLevel(classLevel);
            }
            if (district != null && !district.isEmpty()) {
                user.setDistrict(district);
            }
            if (language != null && !language.isEmpty()) {
                user.setPreferredLanguage(language);
            }
            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            return userRepository.save(user);
        } else {
            // Create new user
            User newUser = new User();
            newUser.setPhone(phone);
            newUser.setName(name != null ? name : "User");
            newUser.setClassLevel(classLevel);
            newUser.setDistrict(district);
            newUser.setPreferredLanguage(language != null ? language : "English");
            newUser.setIsGuest(false);
            if (password != null && !password.isEmpty()) {
                newUser.setPassword(passwordEncoder.encode(password));
            }
            return userRepository.save(newUser);
        }
    }

    // Keep the old method for backward compatibility
    public User createOrGetUser(String phone, String name, String classLevel, String district, String language) {
        return createOrGetUser(phone, name, classLevel, district, language, null);
    }

    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone).orElse(null);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public boolean verifyPassword(User user, String rawPassword) {
        if (user.getPassword() == null || rawPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}