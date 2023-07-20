package com.gotcoder.Myhome.repository;


import com.gotcoder.Myhome.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
