package com.pulpapp.ms_users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.pulpapp.ms_users.entity.User;
import com.pulpapp.ms_users.service.IUserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // GET ALL
    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    // POST
    @PostMapping
    public User create(@RequestBody User user) {
        return userService.save(user);
    }

    // PUT
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        return userService.update(id, user);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}