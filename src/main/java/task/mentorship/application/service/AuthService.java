package task.mentorship.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import task.mentorship.application.entity.*;
import task.mentorship.application.exception.*;
import task.mentorship.application.repository.*;
import task.mentorship.application.security.*;
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, 
    		JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    public String authenticate(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
            		(email, password));

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException("user not found"));

            return jwtTokenProvider.generateToken(user.getEmail());
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid email or password");
        }
    }

    public void validateToken(String token) {
        try {
            String email = jwtTokenProvider.getUsername(token);

            if (!userRepository.existsByEmail(email)) {
                throw new CustomException("Invalid token: User not found");
            }
        } catch (Exception e) {
            throw new CustomException("Invalid token");
        }
    }
}
