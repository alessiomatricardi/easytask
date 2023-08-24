package it.alessiomatricardi.easytask.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import it.alessiomatricardi.easytask.backend.dto.CommentDTO;
import it.alessiomatricardi.easytask.backend.model.Comment;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentDTO entityToDTO(Comment comment);
}
