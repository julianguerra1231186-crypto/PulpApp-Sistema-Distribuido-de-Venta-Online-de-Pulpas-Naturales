package com.pulpapp.ms_users.service;

import java.util.List;
import com.pulpapp.ms_users.entity.User;

public interface IUserService {

    List<User> findAll();

    User findById(Long id);

    User save(User user);

    User update(Long id, User user);

    void delete(Long id);
}