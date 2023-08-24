package it.alessiomatricardi.easytask.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDTO {

    private long id;

    private String description;

    private LocalDateTime createdAt;

    private EmployeeDTO employee;

}
