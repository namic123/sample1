package com.example.sample1.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Todo {
    private Integer id;
    private String todo;
    private LocalDateTime inserted;
    private Integer numOfFiles;
}
