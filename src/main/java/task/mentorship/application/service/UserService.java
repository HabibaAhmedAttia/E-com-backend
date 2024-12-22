package task.mentorship.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import task.mentorship.application.dto.*;
import task.mentorship.application.entity.*;
import task.mentorship.application.exception.*;
import task.mentorship.application.repository.*;




@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;
	

	

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
	}

	public User registerUser(RegisterRequest request) {
        // Check if the email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already in use");
        }

        // Create a new User object
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user = userRepository.save(user);

        Role clientRole = roleRepository.findByName(Role.RoleName.CLIENT)
                .orElseThrow(() -> new CustomException("Role CLIENT not found"));

        user.getRoles().add(clientRole);

        if ("admin@gmail.com".equalsIgnoreCase(user.getEmail())) {
            Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN)
                    .orElseThrow(() -> new CustomException("Role ADMIN not found"));
            user.getRoles().add(adminRole);
        }

        // Save the user again with the assigned roles
        user = userRepository.save(user);

        return user;
    }
	
//	public User registerUser(RegisterRequest request) {
//		if (userRepository.existsByEmail(request.getEmail())) {
//			throw new CustomException("Email already in use");
//		}
//		User user = new User();
//		user.setEmail(request.getEmail());
//		user.setPassword(passwordEncoder.encode(request.getPassword()));
//		user.setName(request.getName());
//		return userRepository.save(user);
//	}

	public boolean emailExists(String email) {
		return userRepository.existsByEmail(email);
	}

	public User updateUser(Long userId, String name) {
	    // Find user by ID
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new CustomException("User not found"));

	    // Update the name
	    user.setName(name);
	    return userRepository.save(user);
	}

	
	public User authenticate(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid credentiald");
        }

        return user;
    }
}

