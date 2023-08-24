package it.alessiomatricardi.easytask.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthEmployeeDTO extends EmployeeDTO {

    private String token;

}
