package com.phatpl.learnvocabulary.mappers;


import java.util.List;


public interface BaseMapper<E, DTO> {
    DTO toDTO(E entity);
    E toEntity(DTO dto);
    List<DTO> toListDTO(List<E> e);
    List<E> toListEntity(List<DTO> dto);
}