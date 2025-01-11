package task.mentorship.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateProductRequest {
	
	@NotNull(message = "Product ID is required")
    private Long id;

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @Min(value = 0, message = "Price must be a positive number")
    private Double price;

    @NotNull(message = "Active status is required")
    private Boolean active;

    private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    
    

}
