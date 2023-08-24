package it.alessiomatricardi.easytask.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import it.alessiomatricardi.easytask.backend.dto.ProjectDTO;
import it.alessiomatricardi.easytask.backend.model.Project;

@Mapper
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    ProjectDTO entityToDTO(Project comment);
}
