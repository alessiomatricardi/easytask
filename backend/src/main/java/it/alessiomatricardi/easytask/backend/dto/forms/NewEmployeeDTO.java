package it.alessiomatricardi.easytask.backend.dto.forms;

import it.alessiomatricardi.easytask.backend.model.EmployeeRole;
import lombok.Getter;

@Getter
public class NewEmployeeDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private EmployeeRole role;

}
