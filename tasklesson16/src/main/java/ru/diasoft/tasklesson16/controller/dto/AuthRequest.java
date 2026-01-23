package ru.diasoft.tasklesson16.controller.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}