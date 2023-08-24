package it.alessiomatricardi.easytask.backend.dto.forms;

import java.time.LocalDateTime;

import it.alessiomatricardi.easytask.backend.model.TaskCategory;
import it.alessiomatricardi.easytask.backend.model.TaskPriority;
import lombok.Getter;

@Getter
public class NewTaskDTO {

    private String description;
    private TaskCategory category;
    private TaskPriority priority;
    private LocalDateTime expectedDeliveryDate;

}
