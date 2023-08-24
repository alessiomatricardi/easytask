package it.alessiomatricardi.easytask.backend.controller;

import java.util.Optional;

import it.alessiomatricardi.easytask.backend.config.JwtService;
import it.alessiomatricardi.easytask.backend.exceptions.EmployeeNotFoundException;
import it.alessiomatricardi.easytask.backend.exceptions.PasswordsMismatchException;
import it.alessiomatricardi.easytask.backend.dto.EmployeeDTO;
import it.alessiomatricardi.easytask.backend.dto.forms.ChangePasswordDTO;
import it.alessiomatricardi.easytask.backend.mapper.EmployeeMapper;
import it.alessiomatricardi.easytask.backend.model.Employee;
import it.alessiomatricardi.easytask.backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    // GET methods

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable("id") long id) {
        // get employee
        Optional<Employee> data = employeeRepository.findById(id);

        // employee not found
        if (data.isEmpty()) {
            throw new EmployeeNotFoundException(id);
        }
        Employee employee = data.get();

        EmployeeDTO employeeToReturn = EmployeeMapper.INSTANCE.entityToDTO(employee);

        return new ResponseEntity<>(employeeToReturn, HttpStatus.OK);
    }

    // POST methods

    // PUT methods

    @PutMapping("/change_password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordDTO changePasswordData) {
        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        Optional<Employee> data = employeeRepository.findByEmail(loggedEmployeeEmail);

        // employee not found
        if (data.isEmpty()) {
            throw new EmployeeNotFoundException(loggedEmployeeEmail);
        }
        Employee employee = data.get();

        String oldPassword = changePasswordData.getOldPassword();
        String newPassword = changePasswordData.getNewPassword();
        String repeatedNewPassword = changePasswordData.getRepeatedNewPassword();

        // check that employee password is equals to the provided old one

        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            throw new BadCredentialsException("Entered password is wrong");
        }

        // check that new password and repeated one are equals
        if (!newPassword.equals(repeatedNewPassword)) {
            throw new PasswordsMismatchException();
        }

        // update password
        String strongPassword = passwordEncoder.encode(newPassword);
        employee.setPassword(strongPassword);

        employeeRepository.save(employee);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // DELETE methods

}
