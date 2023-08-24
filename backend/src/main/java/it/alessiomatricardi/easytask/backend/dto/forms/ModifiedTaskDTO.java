package it.alessiomatricardi.easytask.backend.dto.forms;

import java.time.LocalDateTime;

import it.alessiomatricardi.easytask.backend.model.TaskPriority;
import lombok.Getter;

@Getter
public class ModifiedTaskDTO {

    private String description;
    private TaskPriority priority;
    private LocalDateTime expectedDeliveryDate;

}
