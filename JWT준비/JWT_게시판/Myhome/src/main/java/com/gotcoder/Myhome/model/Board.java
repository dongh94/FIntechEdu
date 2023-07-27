package com.gotcoder.Myhome.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @NotNull
    @Size(min=2, max=30)
    public String title;
    public String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
