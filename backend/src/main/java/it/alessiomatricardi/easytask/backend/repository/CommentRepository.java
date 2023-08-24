package it.alessiomatricardi.easytask.backend.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.alessiomatricardi.easytask.backend.model.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    List<Comment> findByTask_Id(long taskId);

}
