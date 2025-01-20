package com.excelr.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.model.Items;
import com.excelr.model.Users;
import com.excelr.repository.UsersRepository;
import com.excelr.service.ItemsService;
import com.excelr.util.JwtUtil;

@RestController
@CrossOrigin("http://localhost:5173")
public class ItemsController {

    @Autowired
    private ItemsService service;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsersRepository userRepository;

    

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Optional<Users> user = userRepository.findByUsername(username);
        if(user.isPresent()&& user.get().getPassword().equals(password)) {
            Map<String, String> response = new HashMap<>();
            // If Authentication is OK then generate Token
            String token = jwtUtil.generateToken(username);
            response.put("login", "success");
            response.put("token", token);
            response.put("role", user.get().getRole());
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response1 = new HashMap<>();
            response1.put("login", "fail");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response1);
        }
    }

    @PostMapping("/admin/upload/items")
    public ResponseEntity<?> uploadItems(
            @RequestParam String name,
            @RequestParam String brand,
            @RequestParam String category,
            @RequestParam int cost,
            @RequestParam MultipartFile file) {
        // Validate input parameters
        if (name == null || name.isEmpty() ||
            brand == null || brand.isEmpty() ||
            category == null || category.isEmpty() ||
            cost <= 0 || file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input parameters.");
        }

        try {
            // Save the item using the service
            Items savedItem = service.saveItem(name, brand, category, cost, file);
            return ResponseEntity.ok(savedItem);
        } catch (IOException e) {
            // Handle file upload errors
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        } catch (RuntimeException e) {
            // Handle other errors (e.g., database errors)
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving item: " + e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public Users saveUser(@RequestBody Users user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");  
        }
        return userRepository.save(user);  
    }

    @GetMapping("/search")
    public List<Items> search(@RequestParam String category) {
        return service.searchByCategory(category);
    }
}
