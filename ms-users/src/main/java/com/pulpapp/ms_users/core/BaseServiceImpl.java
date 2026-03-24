package com.pulpapp.ms_users.core;

import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<T, D, S, R extends JpaRepository<T, Long>>
        implements IBaseService<T, D, S> {

    protected final R repository;

    // Funciones de mapeo (MapStruct)
    protected abstract Function<T, D> toResponseMapper();
    protected abstract Function<S, T> toEntityMapper();
    protected abstract void updateEntityFromDto(S dto, T entity);

    protected abstract String getEntityName();

    @Override
    public List<D> findAll() {
        return repository.findAll()
                .stream()
                .map(toResponseMapper())
                .collect(Collectors.toList());
    }

    @Override
    public D findById(Long id) {
        T entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(getEntityName() + " not found with id: " + id));

        return toResponseMapper().apply(entity);
    }

    @Override
    public D save(S dto) {
        T entity = toEntityMapper().apply(dto);
        return toResponseMapper().apply(repository.save(entity));
    }

    @Override
    public D update(Long id, S dto) {
        T entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(getEntityName() + " not found with id: " + id));

        updateEntityFromDto(dto, entity);
        return toResponseMapper().apply(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(getEntityName() + " not found with id: " + id);
        }
        repository.deleteById(id);
    }
}