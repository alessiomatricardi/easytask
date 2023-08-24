package it.alessiomatricardi.easytask.backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.alessiomatricardi.easytask.backend.config.JwtService;
import it.alessiomatricardi.easytask.backend.exceptions.*;
import it.alessiomatricardi.easytask.backend.model.*;
import it.alessiomatricardi.easytask.backend.dto.forms.ModifiedTaskDTO;
import it.alessiomatricardi.easytask.backend.dto.forms.NewCommentDTO;
import it.alessiomatricardi.easytask.backend.dto.forms.NewTaskDTO;
import it.alessiomatricardi.easytask.backend.dto.forms.ProjectOrTaskEmployeeDTO;
import it.alessiomatricardi.easytask.backend.mapper.CommentMapper;
import it.alessiomatricardi.easytask.backend.mapper.TaskMapper;
import it.alessiomatricardi.easytask.backend.repository.CommentRepository;
import it.alessiomatricardi.easytask.backend.repository.EmployeeRepository;
import it.alessiomatricardi.easytask.backend.repository.ProjectRepository;
import it.alessiomatricardi.easytask.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.alessiomatricardi.easytask.backend.dto.CommentDTO;
import it.alessiomatricardi.easytask.backend.dto.EmployeeDTO;
import it.alessiomatricardi.easytask.backend.dto.TaskDTO;
import it.alessiomatricardi.easytask.backend.mapper.EmployeeMapper;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;

    private final ProjectRepository projectRepository;

    private final EmployeeRepository employeeRepository;

    private final CommentRepository commentRepository;

    private final JwtService jwtService;

    // GET methods

    @GetMapping("/api/v1/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDTO>> getProjectTasks(
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

        List<TaskDTO> tasks = taskRepository.findByProject_Id(projectId).stream()
                .map(TaskMapper.INSTANCE::entityToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(tasks, HttpStatus.OK);

    }

    @GetMapping("/api/v1/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<TaskDTO> getTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId) {

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

        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }

        TaskDTO taskDto = TaskMapper.INSTANCE.entityToDTO(maybeATask.get());

        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    @GetMapping("/api/v1/projects/{projectId}/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentDTO>> getTaskComments(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        // retrieve the project

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

        // retrieve the task
        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }

        List<CommentDTO> comments = commentRepository.findByTask_Id(taskId).stream()
                .map((comment) -> {
                    EmployeeDTO employeeDTO = EmployeeMapper.INSTANCE.entityToDTO(comment.getEmployee());
                    CommentDTO commentDTO = CommentMapper.INSTANCE.entityToDTO(comment);
                    commentDTO.setEmployee(employeeDTO);
                    return commentDTO;
                }).collect(Collectors.toList());

        return new ResponseEntity<>(comments, HttpStatus.OK);

    }

    @GetMapping("/api/v1/projects/{projectId}/tasks/{taskId}/assignees")
    public ResponseEntity<List<EmployeeDTO>> getTaskAssignees(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        // retrieve the project

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

        // retrieve the task
        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }

        List<EmployeeDTO> assignees = employeeRepository.findByAssignedTasks_Id(taskId).stream()
                .map(EmployeeMapper.INSTANCE::entityToDTO).collect(Collectors.toList());

        return new ResponseEntity<>(assignees, HttpStatus.OK);

    }

    // POST methods

    @PostMapping("/api/v1/projects/{projectId}/tasks")
    public ResponseEntity<?> addTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @RequestBody NewTaskDTO taskData) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        // retrieve the project

        Optional<Project> maybeAProject = projectRepository.findById(projectId);

        if (maybeAProject.isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }
        Project project = maybeAProject.get();

        // check if the employee is the Project Manager
        boolean pmGuard = loggedEmployeeEmail.equals(project.getProjectManager().getEmail());

        if (!pmGuard) {
            throw new ForbiddenOperationException();
        }

        // check if the project is open or not
        if(project.getStatus().equals(ProjectStatus.CLOSED)) {
            throw new ClosedProjectException(projectId);
        }

        Task taskToSave = new Task();
        taskToSave.setDescription(taskData.getDescription());
        taskToSave.setCategory(taskData.getCategory());
        taskToSave.setPriority(taskData.getPriority());
        taskToSave.setExpectedDeliveryDate(taskData.getExpectedDeliveryDate());
        taskToSave.setStatus(TaskStatus.UNASSIGNED);
        Task savedTask = taskRepository.save(taskToSave);

        project.addTask(savedTask);
        projectRepository.save(project);

        TaskDTO taskDto = TaskMapper.INSTANCE.entityToDTO(savedTask);

        return new ResponseEntity<>(taskDto, HttpStatus.CREATED);
    }

    @PostMapping("/api/v1/projects/{projectId}/tasks/propose")
    public ResponseEntity<?> proposeTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @RequestBody NewTaskDTO taskData) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        // retrieve the project

        Optional<Project> maybeAProject = projectRepository.findById(projectId);

        if (maybeAProject.isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }
        Project project = maybeAProject.get();

        // check if the employee is a member
        boolean memberGuard = project.getMembers().stream()
                .anyMatch(member -> member.getEmail().equals(loggedEmployeeEmail));

        if (!memberGuard) {
            throw new ForbiddenOperationException();
        }

        // check if the project is open or not
        if(project.getStatus().equals(ProjectStatus.CLOSED)) {
            throw new ClosedProjectException(projectId);
        }

        Task taskToSave = new Task();
        taskToSave.setDescription(taskData.getDescription());
        taskToSave.setCategory(taskData.getCategory());
        taskToSave.setPriority(taskData.getPriority());
        taskToSave.setExpectedDeliveryDate(taskData.getExpectedDeliveryDate());
        taskToSave.setStatus(TaskStatus.PROPOSED);
        Task savedTask = taskRepository.save(taskToSave);

        project.addTask(savedTask);
        projectRepository.save(project);

        TaskDTO taskDto = TaskMapper.INSTANCE.entityToDTO(savedTask);

        return new ResponseEntity<>(taskDto, HttpStatus.CREATED);
    }
    
    @PostMapping("/api/v1/projects/{projectId}/tasks/{taskId}/assignees")
    public ResponseEntity<?> assignTaskToAMember(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId,
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

        // check if the employee is the Project Manager
        boolean pmGuard = loggedEmployeeEmail.equals(project.getProjectManager().getEmail());

        if (!pmGuard) {
            throw new ForbiddenOperationException();
        }

        // check if the project is open or not
        if(project.getStatus().equals(ProjectStatus.CLOSED)) {
            throw new ClosedProjectException(projectId);
        }

        // retrieve the employee to add
        Optional<Employee> maybeAnEmployee = employeeRepository.findByEmail(employeeData.getEmail());

        if (maybeAnEmployee.isEmpty()) {
            throw new EmployeeNotFoundException(employeeData.getEmail());
        }
        Employee employee = maybeAnEmployee.get();

        // check if the user is trying to add himself
        if (project.getProjectManager().getId().equals(employee.getId())) {
            throw new SelfInsertionException("task");
        }

        // check if the employee is a member of the project
        boolean memberGuard = project.getMembers().stream()
                .anyMatch(member -> member.getId().equals(employee.getId()));

        if (!memberGuard) {
            throw new EmployeeIsNotAMemberException(employee.getEmail());
        }

        // retrieve the task
        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
        Task task = maybeATask.get();

        // check if the task is not approved or not yet
        if(task.getStatus().equals(TaskStatus.PROPOSED)
                || task.getStatus().equals(TaskStatus.REJECTED_PROPOSE)
        ) {
            throw new NotApprovedTaskException(taskId);
        }

        // check if the task is completed
        if(task.getStatus().equals(TaskStatus.COMPLETED)) {
            throw new CompletedTaskException(taskId);
        }

        task.addMember(maybeAnEmployee.get());
        if (task.getStatus().equals(TaskStatus.UNASSIGNED)) {
            task.setStatus(TaskStatus.PENDING);
        }
        taskRepository.save(task);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/api/v1/projects/{projectId}/tasks/{taskId}/comments")
    public ResponseEntity<CommentDTO> commentTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId,
            @RequestBody NewCommentDTO commentData) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        // retrieve the project

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

        // check if the project is open or not
        if(project.getStatus().equals(ProjectStatus.CLOSED)) {
            throw new ClosedProjectException(projectId);
        }

        // retrieve the employee
        Optional<Employee> maybeAnEmployee = employeeRepository.findByEmail(loggedEmployeeEmail);

        if (maybeAnEmployee.isEmpty()) {
            throw new EmployeeNotFoundException(loggedEmployeeEmail);
        }
        Employee employee = maybeAnEmployee.get();

        // retrieve the task
        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
        Task task = maybeATask.get();

        // save the comment

        Comment commentToSave = new Comment();
        commentToSave.setDescription(commentData.getDescription());

        // save the reference inside employee and task

        employee.addComment(commentToSave);
        task.addComment(commentToSave);
        Comment savedComment = commentRepository.save(commentToSave);

        // get member DTO
        EmployeeDTO employeeDTO = EmployeeMapper.INSTANCE.entityToDTO(employee);

        CommentDTO commentDto = CommentMapper.INSTANCE.entityToDTO(savedComment);
        commentDto.setEmployee(employeeDTO);

        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    // PUT methods

    @PutMapping("/api/v1/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<TaskDTO> modifyTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId,
            @RequestBody ModifiedTaskDTO taskData) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        // retrieve the project

        Optional<Project> maybeAProject = projectRepository.findById(projectId);

        if (maybeAProject.isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }
        Project project = maybeAProject.get();

        // check if the employee is the Project Manager
        boolean pmGuard = loggedEmployeeEmail.equals(project.getProjectManager().getEmail());

        if (!pmGuard) {
            throw new ForbiddenOperationException();
        }

        // check if the project is open or not
        if(project.getStatus().equals(ProjectStatus.CLOSED)) {
            throw new ClosedProjectException(projectId);
        }

        // retrieve the task
        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
        Task task = maybeATask.get();

        // check if the task is not approved or not yet
        if(task.getStatus().equals(TaskStatus.PROPOSED)
                || task.getStatus().equals(TaskStatus.REJECTED_PROPOSE)
        ) {
            throw new NotApprovedTaskException(taskId);
        }

        // check if the task is completed
        if(task.getStatus().equals(TaskStatus.COMPLETED)) {
            throw new CompletedTaskException(taskId);
        }

        task.setDescription(taskData.getDescription());
        task.setPriority(taskData.getPriority());
        task.setExpectedDeliveryDate(taskData.getExpectedDeliveryDate());

        TaskDTO taskDto = TaskMapper.INSTANCE.entityToDTO(taskRepository.save(task));

        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    @PutMapping("/api/v1/projects/{projectId}/tasks/{taskId}/start")
    public ResponseEntity<TaskDTO> startTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId) {

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

        // retrieve the task
        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
        Task task = maybeATask.get();

        // check if the employee is assigned to the task
        boolean memberGuard = task.getAssignedMembers().stream()
                .anyMatch(member -> member.getEmail().equals(loggedEmployeeEmail));

        if (!memberGuard) {
            throw new ForbiddenOperationException();
        }

        // check if the task is not approved or not yet
        if(task.getStatus().equals(TaskStatus.PROPOSED)
                || task.getStatus().equals(TaskStatus.REJECTED_PROPOSE)
        ) {
            throw new NotApprovedTaskException(taskId);
        }

        // check if the task is completed
        if(task.getStatus().equals(TaskStatus.COMPLETED)) {
            throw new CompletedTaskException(taskId);
        }

        // check if the task is pending
        if(!task.getStatus().equals(TaskStatus.PENDING)) {
            throw new InvalidTaskStatusChangeException(
                    taskId,
                    task.getStatus().name(),
                    TaskStatus.STARTED.name()
            );
        }

        task.setStatus(TaskStatus.STARTED);
        taskRepository.save(task);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/api/v1/projects/{projectId}/tasks/{taskId}/complete")
    public ResponseEntity<TaskDTO> setTaskAsCompleted(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId) {

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

        // retrieve the task
        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
        Task task = maybeATask.get();

        // check if the employee is assigned to the task
        boolean memberGuard = task.getAssignedMembers().stream()
                .anyMatch(member -> member.getEmail().equals(loggedEmployeeEmail));

        if (!memberGuard) {
            throw new ForbiddenOperationException();
        }

        // check if the task is not approved or not yet
        if(task.getStatus().equals(TaskStatus.PROPOSED)
                || task.getStatus().equals(TaskStatus.REJECTED_PROPOSE)
        ) {
            throw new NotApprovedTaskException(taskId);
        }

        // check if the task is completed
        if(task.getStatus().equals(TaskStatus.COMPLETED)) {
            throw new CompletedTaskException(taskId);
        }

        // check if the task is started
        if(!task.getStatus().equals(TaskStatus.STARTED)) {
            throw new InvalidTaskStatusChangeException(
                    taskId,
                    task.getStatus().name(),
                    TaskStatus.COMPLETED.name()
            );
        }

        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        taskRepository.save(task);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/api/v1/projects/{projectId}/tasks/{taskId}/reopen")
    public ResponseEntity<TaskDTO> reopenTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId) {

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

        // retrieve the task
        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
        Task task = maybeATask.get();

        // check if the employee is assigned to the task
        boolean memberGuard = task.getAssignedMembers().stream()
                .anyMatch(member -> member.getEmail().equals(loggedEmployeeEmail));

        if (!memberGuard) {
            throw new ForbiddenOperationException();
        }

        // check if the task is not approved or not yet
        if(task.getStatus().equals(TaskStatus.PROPOSED)
                || task.getStatus().equals(TaskStatus.REJECTED_PROPOSE)
        ) {
            throw new NotApprovedTaskException(taskId);
        }

        // check if the task is completed
        if(!task.getStatus().equals(TaskStatus.COMPLETED)) {
            throw new InvalidTaskStatusChangeException(
                    taskId,
                    task.getStatus().name(),
                    TaskStatus.PENDING.name()
            );
        }

        task.setStatus(TaskStatus.PENDING);
        task.setCompletedAt(null);
        taskRepository.save(task);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/api/v1/projects/{projectId}/tasks/{taskId}/accept")
    public ResponseEntity<TaskDTO> acceptProposedTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId) {

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

        // retrieve the task
        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
        Task task = maybeATask.get();

        // check if the employee is the Project Manager
        boolean pmGuard = loggedEmployeeEmail.equals(project.getProjectManager().getEmail());

        if (!pmGuard) {
            throw new ForbiddenOperationException();
        }

        // check if the task is proposed
        if(!task.getStatus().equals(TaskStatus.PROPOSED)) {
            throw new InvalidTaskStatusChangeException(
                    taskId,
                    task.getStatus().name(),
                    TaskStatus.UNASSIGNED.name()
            );
        }

        task.setStatus(TaskStatus.UNASSIGNED);
        taskRepository.save(task);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/api/v1/projects/{projectId}/tasks/{taskId}/reject")
    public ResponseEntity<TaskDTO> rejectProposedTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId) {

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

        // retrieve the task
        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
        Task task = maybeATask.get();

        // check if the employee is the Project Manager
        boolean pmGuard = loggedEmployeeEmail.equals(project.getProjectManager().getEmail());

        if (!pmGuard) {
            throw new ForbiddenOperationException();
        }

        // check if the task is proposed
        if(!task.getStatus().equals(TaskStatus.PROPOSED)) {
            throw new InvalidTaskStatusChangeException(
                    taskId,
                    task.getStatus().name(),
                    TaskStatus.REJECTED_PROPOSE.name()
            );
        }

        task.setStatus(TaskStatus.REJECTED_PROPOSE);
        taskRepository.save(task);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // DELETE methods

    @DeleteMapping("/api/v1/projects/{projectId}/tasks/{taskId}/assignees/{memberId}")
    public ResponseEntity<?> removeMemberFromAssignedTask(
            @RequestHeader("Authorization") String authHeader, 
            @PathVariable("projectId") long projectId,
            @PathVariable("taskId") long taskId,
            @PathVariable("memberId") long memberId) {

        // get employee email from jwt
        String token = authHeader.substring(7);
        String loggedEmployeeEmail = jwtService.extractUsername(token);

        // retrieve the project

        Optional<Project> maybeAProject = projectRepository.findById(projectId);

        if (maybeAProject.isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }
        Project project = maybeAProject.get();

        // check if the employee is the Project Manager
        boolean pmGuard = loggedEmployeeEmail.equals(project.getProjectManager().getEmail());

        if (!pmGuard) {
            throw new ForbiddenOperationException();
        }

        // check if the project is open or not
        if(project.getStatus().equals(ProjectStatus.CLOSED)) {
            throw new ClosedProjectException(projectId);
        }

        // check if the user is trying to remove himself
        if (project.getProjectManager().getId() == memberId) {
            throw new SelfDeletionException("task");
        }

        // retrieve the task
        Optional<Task> maybeATask = taskRepository.findByIdAndProject_Id(taskId, projectId);

        if (maybeATask.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
        Task task = maybeATask.get();

        // check if the task is not approved or not yet
        if(task.getStatus().equals(TaskStatus.PROPOSED)
                || task.getStatus().equals(TaskStatus.REJECTED_PROPOSE)
        ) {
            throw new NotApprovedTaskException(taskId);
        }

        // check if the task is completed
        if(task.getStatus().equals(TaskStatus.COMPLETED)) {
            throw new CompletedTaskException(taskId);
        }

        // retrieve the employee to remove
        Optional<Employee> maybeAnEmployee = employeeRepository.findById(memberId);

        if (maybeAnEmployee.isEmpty()) {
            throw new EmployeeNotFoundException(memberId);
        }
        Employee employee = maybeAnEmployee.get();

        task.removeMember(employee);
        if (task.getAssignedMembers().size() == 0)
            task.setStatus(TaskStatus.UNASSIGNED);
        taskRepository.save(task);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
