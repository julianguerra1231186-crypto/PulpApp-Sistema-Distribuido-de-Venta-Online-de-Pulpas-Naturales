package com.pulpapp.ms_users.core;

import com.pulpapp.ms_users.mapper.EntityMapper;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<T, D, S, R extends JpaRepository<T, Long>>
        implements IBaseService<T, D, S> {

    protected final R repository;
    protected final EntityMapper<T, D, S> mapper;

    protected abstract String getEntityName();

    @Override
    public List<D> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    public D findById(Long id) {
        T entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(getEntityName() + " not found with id: " + id));

        return mapper.toResponseDto(entity);
    }

    @Override
    public D save(S dto) {
        T entity = mapper.toEntity(dto);
        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    public D update(Long id, S dto) {
        T entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(getEntityName() + " not found with id: " + id));

        mapper.updateEntityFromDto(dto, entity);
        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(getEntityName() + " not found with id: " + id);
        }
        repository.deleteById(id);
    }
}