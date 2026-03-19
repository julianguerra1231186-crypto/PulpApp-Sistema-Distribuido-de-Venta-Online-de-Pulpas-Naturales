package com.pulpapp.ms_users.core;

import java.util.List;

public interface IBaseService<T, D, S> {

    List<D> findAll();

    D findById(Long id);

    D save(S dto);

    D update(Long id, S dto);

    void delete(Long id);
}