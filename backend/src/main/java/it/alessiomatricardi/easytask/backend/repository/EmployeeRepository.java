package it.alessiomatricardi.easytask.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.alessiomatricardi.easytask.backend.model.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    List<Employee> findByWorkingProjects_Id(long projectId);

    List<Employee> findByAssignedTasks_Id(long taskId);

}
