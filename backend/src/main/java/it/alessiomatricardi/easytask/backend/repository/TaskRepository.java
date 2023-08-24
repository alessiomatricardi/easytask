package it.alessiomatricardi.easytask.backend.repository;

import java.util.List;
import java.util.Optional;

import it.alessiomatricardi.easytask.backend.model.Task;
import it.alessiomatricardi.easytask.backend.model.TaskStatus;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {

    List<Task> findByProject_Id(long projectId);

    Optional<Task> findByIdAndProject_Id(long taskId, long projectId);

    List<Task> findByProject_IdAndAssignedMembers_IdAndStatusIsNot(long projectId, long memberId, TaskStatus status);

}
