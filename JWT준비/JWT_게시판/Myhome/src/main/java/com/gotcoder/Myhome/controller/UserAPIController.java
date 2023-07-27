package com.gotcoder.Myhome.controller;

import com.gotcoder.Myhome.model.Board;
import com.gotcoder.Myhome.model.User;
import com.gotcoder.Myhome.repository.UserRepository;
import com.gotcoder.Myhome.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
public class UserAPIController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> users() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public User newUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/users/{id}")
    public User one(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @PutMapping("/users/{id}")
    public User replaceUser(@RequestBody User user, @PathVariable Long id) {
        return userRepository.findById(id)
                .map(one -> {

//                    one.setTitle(user.getTitle());
//                    one.setContent(user.getContent());
                    one.getBoards().clear();
                    one.getBoards().addAll(user.getBoards());
                    for (Board board : one.getBoards()) {
                        board.setUser(one);
                    }
                    return userRepository.save(one);
                })
                .orElseGet(() -> {
                    user.setId(id);
                    return userRepository.save(user);
                });
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
