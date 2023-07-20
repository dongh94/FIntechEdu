package com.gotcoder.Myhome.controller;

import com.gotcoder.Myhome.model.Board;
import com.gotcoder.Myhome.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1/board")
public class BoardController {

    @Autowired
    public BoardRepository boardRepository;

    @GetMapping("/list")
    public String boradList(Model model) {
        List<Board> boardList = boardRepository.findAll();
        model.addAttribute("boardList", boardList);
        return "board/list";
    }
}
