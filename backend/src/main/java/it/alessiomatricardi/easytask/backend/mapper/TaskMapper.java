package it.alessiomatricardi.easytask.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import it.alessiomatricardi.easytask.backend.dto.TaskDTO;
import it.alessiomatricardi.easytask.backend.model.Task;

@Mapper
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskDTO entityToDTO(Task comment);
}
