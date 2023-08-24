package it.alessiomatricardi.easytask.backend.controller;

import java.util.Optional;

import it.alessiomatricardi.easytask.backend.config.JwtService;
import it.alessiomatricardi.easytask.backend.dto.AuthEmployeeDTO;
import it.alessiomatricardi.easytask.backend.dto.forms.NewEmployeeDTO;
import it.alessiomatricardi.easytask.backend.exceptions.EmployeeAlreadyExistsException;
import it.alessiomatricardi.easytask.backend.exceptions.EmployeeNotFoundException;
import it.alessiomatricardi.easytask.backend.dto.EmployeeDTO;
import it.alessiomatricardi.easytask.backend.dto.forms.AuthDTO;
import it.alessiomatricardi.easytask.backend.mapper.EmployeeMapper;
import it.alessiomatricardi.easytask.backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import it.alessiomatricardi.easytask.backend.model.Employee;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmployeeRepository employeeRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthEmployeeDTO> authenticate(@RequestBody AuthDTO authData) {
        // check if the user exists
        Optional<Employee> maybeAnEmployee = employeeRepository.findByEmail(authData.getEmail());

        if (maybeAnEmployee.isEmpty()) {
            throw new EmployeeNotFoundException(authData.getEmail());
        }
        Employee employee = maybeAnEmployee.get();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authData.getEmail(),
                        authData.getPassword()
                )
        );

        // generate the JWT
        String jwtToken = jwtService.generateToken(employee);

        // returns a dto
        AuthEmployeeDTO employeeDto = EmployeeMapper.INSTANCE.entityToAuthDTO(employee);
        employeeDto.setToken(jwtToken);

        return new ResponseEntity<>(employeeDto, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<EmployeeDTO> register(@RequestBody NewEmployeeDTO newEmployeeData) {
        // check if the new user inserted an email already present
        Optional<Employee> maybeAnEmployee = employeeRepository.findByEmail(newEmployeeData.getEmail());
        if (maybeAnEmployee.isPresent()) {
            throw new EmployeeAlreadyExistsException(newEmployeeData.getEmail());
        }

        Employee employeeToSave = new Employee();

        employeeToSave.setFirstName(newEmployeeData.getFirstName());
        employeeToSave.setLastName(newEmployeeData.getLastName());
        employeeToSave.setEmail(newEmployeeData.getEmail());
        String strongPassword = passwordEncoder.encode(newEmployeeData.getPassword());
        employeeToSave.setPassword(strongPassword);
        employeeToSave.setRole(newEmployeeData.getRole());

        Employee savedEmployee = employeeRepository.save(employeeToSave);

        EmployeeDTO employeeToReturn = EmployeeMapper.INSTANCE.entityToDTO(savedEmployee);

        return new ResponseEntity<>(employeeToReturn, HttpStatus.CREATED);
    }

}
