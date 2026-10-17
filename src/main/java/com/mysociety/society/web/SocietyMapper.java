package com.mysociety.society.web;

import com.mysociety.society.domain.Society;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SocietyMapper {
    SocietyController.SocietyResponse toResponse(Society society);
}
