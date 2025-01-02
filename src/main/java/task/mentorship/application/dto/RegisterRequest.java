package task.mentorship.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
	@NotBlank(message = "Email is required")
    @Email(message = "Email should be in a valid format")
    private String email;
	@NotBlank(message = "Password is required")
    @Size(min = 4, max = 10, message = "Password must be between 4 and 10 characters")
    private String password;
	@NotBlank(message = "Name is required")
    private String name;
    
    public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
