package com.gotcoder.Myhome.service;

import com.gotcoder.Myhome.model.Board;
import com.gotcoder.Myhome.model.User;
import com.gotcoder.Myhome.repository.BoardRepository;
import com.gotcoder.Myhome.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    @Autowired private BoardRepository boardRepository;

    @Autowired private UserRepository userRepository;

    public Board save(String username, Board board) {
        User user = userRepository.findByUsername(username);
        board.setUser(user);
        return boardRepository.save(board);
    }

}
