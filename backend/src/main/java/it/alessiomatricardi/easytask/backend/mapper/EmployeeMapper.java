package it.alessiomatricardi.easytask.backend.mapper;

import it.alessiomatricardi.easytask.backend.dto.AuthEmployeeDTO;
import it.alessiomatricardi.easytask.backend.dto.EmployeeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import it.alessiomatricardi.easytask.backend.model.Employee;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    EmployeeDTO entityToDTO(Employee employee);

    AuthEmployeeDTO entityToAuthDTO(Employee employee);
}
