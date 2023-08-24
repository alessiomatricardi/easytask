package it.alessiomatricardi.easytask.backend.dto;

import java.time.LocalDateTime;

import it.alessiomatricardi.easytask.backend.model.TaskCategory;
import it.alessiomatricardi.easytask.backend.model.TaskPriority;
import it.alessiomatricardi.easytask.backend.model.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {

    private long id;

    private String description;

    private TaskStatus status;

    private TaskCategory category;

    private TaskPriority priority;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime expectedDeliveryDate;

    private LocalDateTime completedAt;

}
