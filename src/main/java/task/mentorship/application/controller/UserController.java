package task.mentorship.application.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import task.mentorship.application.security.*;
import task.mentorship.application.entity.*;
import task.mentorship.application.exception.CustomException;
import task.mentorship.application.dto.*;
import task.mentorship.application.service.*;
@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final JwtTokenProvider jwt;


    public UserController(UserService userService, AuthService authService, JwtTokenProvider jwt) {
		super();
		this.userService = userService;
		this.authService = authService;
		this.jwt = jwt;
	}
	@PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    	if (userService.emailExists(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Email already in use");
        }
    	userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully");
    }
	
	
	@PostMapping("/login")
	public ResponseEntity<?> login(
	        @RequestHeader("email") String email, 
	        @RequestHeader("password") String password) {
	    try {
	        String token = authService.authenticate(email, password);
	        return ResponseEntity.ok(Map.of("token", token));
	    } catch (CustomException e) {
	        return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
	    }
	}
	
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        try {
//            String token = authService.authenticate(request.getEmail(), request.getPassword());
//            return ResponseEntity.ok(Map.of("token", token));
//        } catch (CustomException e) {
//            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
//        }
//    }
	
	
	@PutMapping("/users/update")
	public ResponseEntity<?> updateUser(
	        @RequestHeader("Authorization") String token, 
	        @RequestBody Map<String, Object> requestData) {
	    try {
	        String email = jwt.getUsername(token.replace("Bearer ", ""));
	        Long userId = Long.parseLong(requestData.get("user_id").toString());
	        String name = requestData.get("name").toString();
	        User updatedUser = userService.updateUser(userId, name);
	        return ResponseEntity.ok(Map.of(
	                "message", "User updated successfully",
	                "user", updatedUser
	        ));
	    } catch (CustomException e) {
	        return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
	    }
	}

	
}
