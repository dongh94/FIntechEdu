package com.gotcoder.Myhome.controller;

import com.gotcoder.Myhome.model.Board;
import com.gotcoder.Myhome.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
public class BoardAPIController {

    @Autowired
    private BoardRepository boardRepository;

    @GetMapping("/boards")
    public List<Board> boards(@RequestParam(required = false, defaultValue = "") String title,
                              @RequestParam(required = false, defaultValue = "") String content) {
        if (StringUtils.isEmpty(title) && StringUtils.isEmpty(content)) {
            return boardRepository.findAll();
        } else {
            return boardRepository.findByTitleOrContent(title, content);
        }
    }

    @PostMapping("/boards")
    public Board newBoard(@RequestBody Board board) {
        return boardRepository.save(board);
    }

    @GetMapping("/boards/{id}")
    public Board one(@PathVariable Long id) {
        return boardRepository.findById(id).orElse(null);
    }

    @PutMapping("/boards/{id}")
    public Board replaceBoard(@RequestBody Board board, @PathVariable Long id) {
        return boardRepository.findById(id)
                .map(one -> {
                    one.setTitle(board.getTitle());
                    one.setContent(board.getContent());
                    return boardRepository.save(one);
                })
                .orElseGet(() -> {
                    board.setId(id);
                    return boardRepository.save(board);
                });
    }

    @DeleteMapping("/boards/{id}")
    public void deleteBoard(@PathVariable Long id) {
        boardRepository.deleteById(id);
    }
}
