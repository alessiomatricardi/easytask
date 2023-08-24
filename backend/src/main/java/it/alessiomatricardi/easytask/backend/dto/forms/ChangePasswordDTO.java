package it.alessiomatricardi.easytask.backend.dto.forms;

import lombok.Getter;

@Getter
public class ChangePasswordDTO {

    private String oldPassword;
    private String newPassword;
    private String repeatedNewPassword;

}
