package it.alessiomatricardi.easytask.backend.dto;

import it.alessiomatricardi.easytask.backend.model.EmployeeRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDTO {

    private long id;

    private String firstName;

    private String lastName;

    private String email;

    private EmployeeRole role;

}