package it.alessiomatricardi.easytask.backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.alessiomatricardi.easytask.backend.config.JwtService;
import it.alessiomatricardi.easytask.backend.exceptions.*;
import it.alessiomatricardi.easytask.backend.dto.forms.NewProjectDTO;
import it.alessiomatricardi.easytask.backend.dto.forms.ProjectOrTaskEmployeeDTO;
import it.alessiomatricardi.easytask.backend.exceptions.*;
import it.alessiomatricardi.easytask.backend.mapper.ProjectMapper;
import it.alessiomatricardi.easytask.backend.repository.EmployeeRepository;
import it.alessiomatricardi.easytask.backend.repository.ProjectRepository;
import it.alessiomatricardi.easytask.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.alessiomatricardi.easytask.backend.dto.EmployeeDTO;
import it.alessiomatricardi.easytask.backend.dto.ProjectDTO;
import it.alessiomatricardi.easytask.backend.mapper.EmployeeMapper;
import it.alessiomatricardi.easytask.backend.model.Employee;
import it.alessiomatricardi.easytask.backend.model.EmployeeRole;
import it.alessiomatricardi.easytask.backend.model.Project;
import it.alessiomatricardi.easytask.backend.model.ProjectStatus;
import it.alessiomatricardi.easytask.backend.model.Task;
import it.alessiomatricardi.easytask.backend.model.TaskStatus;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectRepository projectRepository;

    private final EmployeeRepository employeeRepository;

    private final TaskRepository taskRepository;

    private final JwtService jwtService;

    // GET methods

    @GetMapping("/managing")
    public ResponseEntity<List<ProjectDTO>> getManagingProjects(@RequestHeader("Authorization") String authHeader) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        List<ProjectDTO> projectsList = projectRepository.findByProjectManager_Email(
                loggedEmployeeEmail).stream()
                .map((project) -> {
                    EmployeeDTO employeeDTO = EmployeeMapper.INSTANCE.entityToDTO(project.getProjectManager());
                    ProjectDTO projectDTO = ProjectMapper.INSTANCE.entityToDTO(project);
                    projectDTO.setProjectManager(employeeDTO);
                    return projectDTO;
                }).collect(Collectors.toList());

        return new ResponseEntity<>(projectsList, HttpStatus.OK);
    }

    @GetMapping("/working")
    public ResponseEntity<List<ProjectDTO>> getWorkingProjects(@RequestHeader("Authorization") String authHeader) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        List<ProjectDTO> projects = projectRepository.findByMembers_Email(loggedEmployeeEmail).stream()
                .map((project) -> {
                    EmployeeDTO employeeDTO = EmployeeMapper.INSTANCE.entityToDTO(project.getProjectManager());
                    ProjectDTO projectDTO = ProjectMapper.INSTANCE.entityToDTO(project);
                    projectDTO.setProjectManager(employeeDTO);
                    return projectDTO;
                }).collect(Collectors.toList());

        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProject(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        Optional<Project> maybeAProject = projectRepository.findById(projectId);

        if (maybeAProject.isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }
        Project project = maybeAProject.get();

        // check if the employee is the Project Manager
        boolean pmGuard = loggedEmployeeEmail.equals(project.getProjectManager().getEmail());

        if (!pmGuard) {
            // check if the employee is a member

            boolean memberGuard = project.getMembers().stream()
                    .anyMatch(member -> member.getEmail().equals(loggedEmployeeEmail));

            if (!memberGuard) {
                throw new ForbiddenOperationException();
            }
        }

        // get project manager DTO
        EmployeeDTO employeeDTO = EmployeeMapper.INSTANCE.entityToDTO(project.getProjectManager());

        ProjectDTO projectDto = ProjectMapper.INSTANCE.entityToDTO(project);
        projectDto.setProjectManager(employeeDTO);

        return new ResponseEntity<ProjectDTO>(projectDto, HttpStatus.OK);
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<EmployeeDTO>> getProjectMembers(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        Optional<Project> maybeAProject = projectRepository.findById(projectId);

        if (maybeAProject.isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }
        Project project = maybeAProject.get();

        // check if the employee is the Project Manager
        boolean pmGuard = loggedEmployeeEmail.equals(project.getProjectManager().getEmail());

        if (!pmGuard) {
            // check if the employee is a member

            boolean memberGuard = project.getMembers().stream()
                    .anyMatch(member -> member.getEmail().equals(loggedEmployeeEmail));

            if (!memberGuard) {
                throw new ForbiddenOperationException();
            }
        }

        List<EmployeeDTO> members = employeeRepository.findByWorkingProjects_Id(projectId).stream()
                .map(EmployeeMapper.INSTANCE::entityToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    // POST methods

    @PostMapping("")
    public ResponseEntity<ProjectDTO> createProject(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody NewProjectDTO newProjectData) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        // check if the user exists
        Optional<Employee> maybeAnEmployee = employeeRepository.findByEmail(loggedEmployeeEmail);
        if(maybeAnEmployee.isEmpty()) {
            throw new EmployeeNotFoundException(loggedEmployeeEmail);
        }
        Employee employee = maybeAnEmployee.get();

        // check if the user is a project manager
        if (employee.getRole().compareTo(EmployeeRole.PROJECT_MANAGER) != 0) {
            throw new ForbiddenOperationException();
        }

        // check if already exists a project with the same name
        Optional<Project> maybeAProject = projectRepository.findByName(newProjectData.getName());
        if(maybeAProject.isPresent()) {
            throw new ProjectAlreadyExistsException(newProjectData.getName());
        }

        Project projectToSave = new Project();
        projectToSave.setName(newProjectData.getName());
        projectToSave.setStatus(ProjectStatus.OPEN);
        employee.addManagingProject(projectToSave);

        Project savedProject = projectRepository.save(projectToSave);

        // get project manager DTO
        EmployeeDTO employeeDTO = EmployeeMapper.INSTANCE.entityToDTO(savedProject.getProjectManager());

        ProjectDTO projectDto = ProjectMapper.INSTANCE.entityToDTO(savedProject);
        projectDto.setProjectManager(employeeDTO);

        return new ResponseEntity<>(projectDto, HttpStatus.CREATED);
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<?> addMemberToProject(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @RequestBody ProjectOrTaskEmployeeDTO employeeData) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        // retrieve the project
        Optional<Project> maybeAProject = projectRepository.findById(projectId);

        if (maybeAProject.isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }
        Project project = maybeAProject.get();

        // check if the project is open or not
        if(project.getStatus().equals(ProjectStatus.CLOSED)) {
            throw new ClosedProjectException(projectId);
        }

        // check if the user exists
        Optional<Employee> maybeAnEmployee = employeeRepository.findByEmail(loggedEmployeeEmail);
        if(maybeAnEmployee.isEmpty()) {
            throw new EmployeeNotFoundException(loggedEmployeeEmail);
        }
        Employee employee = maybeAnEmployee.get();

        // check if the user is the project manager of the project
        if (!project.getProjectManager().getId().equals(employee.getId())) {
            throw new ForbiddenOperationException();
        }

        // retrieve the employee to add
        maybeAnEmployee = employeeRepository.findByEmail(employeeData.getEmail());

        if(maybeAnEmployee.isEmpty()) {
            throw new EmployeeNotFoundException(employeeData.getEmail());
        }
        employee = maybeAnEmployee.get();

        // check if the user is trying to add himself
        if (project.getProjectManager().getId().equals(employee.getId())) {
            throw new SelfInsertionException("project");
        }

        project.addMember(maybeAnEmployee.get());
        projectRepository.save(project);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // PUT methods

    @PutMapping("/{projectId}/close")
    public ResponseEntity<?> closeProject(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        // retrieve the project
        Optional<Project> maybeAProject = projectRepository.findById(projectId);

        if (maybeAProject.isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }
        Project project = maybeAProject.get();

        // check if the user exists
        Optional<Employee> maybeAnEmployee = employeeRepository.findByEmail(loggedEmployeeEmail);
        if(maybeAnEmployee.isEmpty()) {
            throw new EmployeeNotFoundException(loggedEmployeeEmail);
        }
        Employee employee = maybeAnEmployee.get();

        // check if the user is the project manager of the project
        if (!project.getProjectManager().getId().equals(employee.getId())) {
            throw new ForbiddenOperationException();
        }

        project.setStatus(ProjectStatus.CLOSED);
        project.setClosedAt(LocalDateTime.now());

        projectRepository.save(project);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    // DELETE methods

    @DeleteMapping("/{projectId}/members/{employeeId}")
    public ResponseEntity<?> removeMemberFromProject(@RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId, @PathVariable("employeeId") long employeeId) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        // retrieve the project
        Optional<Project> maybeAProject = projectRepository.findById(projectId);

        if (maybeAProject.isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }
        Project project = maybeAProject.get();

        // check if the project is open or not
        if(project.getStatus().equals(ProjectStatus.CLOSED)) {
            throw new ClosedProjectException(projectId);
        }

        // check if the user exists
        Optional<Employee> maybeAnEmployee = employeeRepository.findByEmail(loggedEmployeeEmail);
        if(maybeAnEmployee.isEmpty()) {
            throw new EmployeeNotFoundException(loggedEmployeeEmail);
        }
        Employee employee = maybeAnEmployee.get();

        // check if the user is the project manager of the project
        if (!project.getProjectManager().getId().equals(employee.getId())) {
            throw new ForbiddenOperationException();
        }

        // check if the user is trying to remove himself
        if (project.getProjectManager().getId() == employeeId) {
            throw new SelfDeletionException("project");
        }

        // retieve the employee to remove
        maybeAnEmployee = employeeRepository.findById(employeeId);

        if(maybeAnEmployee.isEmpty()) {
            throw new EmployeeNotFoundException(loggedEmployeeEmail);
        }

        // search if the employee has assigned tasks
        List<Task> employeeTasks = taskRepository.findByProject_IdAndAssignedMembers_IdAndStatusIsNot(
                project.getId(),
                employeeId,
                TaskStatus.COMPLETED
        );

        if (!employeeTasks.isEmpty()) {
            throw new MemberHasAssignedTasksException(maybeAnEmployee.get().getEmail());
        }

        project.removeMember(maybeAnEmployee.get());
        projectRepository.save(project);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
