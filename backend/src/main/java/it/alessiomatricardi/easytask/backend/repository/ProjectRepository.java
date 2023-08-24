package it.alessiomatricardi.easytask.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.alessiomatricardi.easytask.backend.model.Project;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    Optional<Project> findByName(String name);

    // find all projects where id is the Project Manager
    List<Project> findByProjectManager_Id(long id);

    // find all projects where email is the Project Manager
    List<Project> findByProjectManager_Email(String email);

    // find all projects where employeeId is a member
    List<Project> findByMembers_Id(long employeeId);

    // find all projects where employeeEmail is a member
    List<Project> findByMembers_Email(String employeeEmail);

}
