package task.mentorship.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PaginationRequest {
	
	@NotNull(message = "Page size is required")
    @Min(value = 1, message = "Page size must be greater than 0")
    private Integer pageSize;

    @NotNull(message = "Offset is required")
    @Min(value = 0, message = "Offset must be 0 or greater")
    private Integer offset;

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}
    
    


}
