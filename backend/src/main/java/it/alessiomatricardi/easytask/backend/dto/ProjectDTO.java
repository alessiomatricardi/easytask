package it.alessiomatricardi.easytask.backend.dto;

import java.time.LocalDateTime;

import it.alessiomatricardi.easytask.backend.model.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDTO {

    private long id;

    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;

    private ProjectStatus status;

    private EmployeeDTO projectManager;

}
