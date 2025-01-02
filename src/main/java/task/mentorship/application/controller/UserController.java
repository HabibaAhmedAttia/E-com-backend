package task.mentorship.application.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


import org.springframework.beans.factory.annotation.Value;

import task.mentorship.application.security.*;
import task.mentorship.application.entity.*;
import task.mentorship.application.exception.CustomException;
import task.mentorship.application.dto.*;
import task.mentorship.application.service.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${app.frontend.origin}")
@Tag(name = "Auth")
public class UserController {
	private final UserService userService;
	private final AuthService authService;
	private final JwtTokenProvider jwt;

	@Value("${app.frontend.origin}")
	private String frontendOrigin;

	public UserController(UserService userService, AuthService authService, JwtTokenProvider jwt) {
		super();
		this.userService = userService;
		this.authService = authService;
		this.jwt = jwt;
	}
	@PostMapping("/register")
	@Operation(
	        summary = "Register a new user",
	        description = "Registers a user by providing name, email, password",
	        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
	            description = "User registration details",
	            required = true,
	            content = @Content(
	                schema = @Schema(implementation = RegisterRequest.class)
	            )
	        ),
	        responses = {
	            @ApiResponse(responseCode = "200", description = "User registered successfully"),
	            @ApiResponse(responseCode = "400", description = "Email already in use"),
	            @ApiResponse(responseCode = "403", description = "validation error")
	        }
	    )
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
		Map<String, String> response = new HashMap<>();
		if (userService.emailExists(request.getEmail())) {
			response.put("message", "Email already in use");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		userService.registerUser(request);
		response.put("message", "User registered successfully");
		return ResponseEntity.ok(response);
	}

	@PostMapping("/login")
	@Operation(
	        summary = "User login",
	        description = "Authenticates a user by email and password and returns a JWT token.",
	        parameters = {
	            @Parameter(
	                name = "email",
	                description = "User's email address",
	                required = true,
	                example = "user@example.com"
	            ),
	            @Parameter(
	                name = "password",
	                description = "User's password",
	                required = true,
	                example = "password123"
	            )
	        },
	        responses = {
	            @ApiResponse(responseCode = "200", description = "Authentication successful - JWT token returned"),
	            @ApiResponse(responseCode = "401", description = "Authentication failed - Invalid credentials")
	        }
	    )
	public ResponseEntity<?> login(@RequestHeader("email") String email, @RequestHeader("password") String password) {
		try {
			String token = authService.authenticate(email, password);
			return ResponseEntity.ok(Map.of("token", token));
		} catch (CustomException e) {
			return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/users/update")
	@Operation(
	        summary = "Update user details",
	        description = "Updates the details of a user. Requires an Authorization token in the header and a JSON payload in the request body.",
	        parameters = {
	            @Parameter(
	                name = "Authorization",
	                description = "Bearer token for authorization",
	                required = true,
	                example = "Bearer <JWT>"
	            )
	        },
	        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
	            description = "JSON payload containing user_id and name to be updated",
	            required = true,
	            content = @Content(
	                schema = @Schema(implementation = UpdateRequest.class)
	            )
	        ),
	        responses = {
	            @ApiResponse(responseCode = "200", description = "User updated successfully"),
	            @ApiResponse(responseCode = "400", description = "Invalid input or user not found"),
	            @ApiResponse(responseCode = "500", description = "Unexpected server error")
	        },security = @SecurityRequirement(name = "bearerAuth")
	    )
	public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
			@RequestBody Map<String, Object> requestData) {
		try {
			String email = jwt.getUsername(token.replace("Bearer ", ""));
			Long userId = Long.parseLong(requestData.get("user_id").toString());
			String name = requestData.get("name").toString();
			User updatedUser = userService.updateUser(userId, name);
			return ResponseEntity.ok(Map.of("message", "User updated successfully", "user", updatedUser));
		} catch (CustomException e) {
			return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
		}
	}

}
