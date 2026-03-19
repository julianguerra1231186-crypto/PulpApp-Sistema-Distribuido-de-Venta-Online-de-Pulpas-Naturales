package com.pulpapp.ms_users.mapper;

public interface EntityMapper<T, D, S> {

    D toResponseDto(T entity);

    T toEntity(S dto);

    void updateEntityFromDto(S dto, T entity);
}