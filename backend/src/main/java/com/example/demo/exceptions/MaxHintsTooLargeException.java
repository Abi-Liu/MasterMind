package com.example.demo.exceptions;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MaxHintsTooLargeException extends RuntimeException {
    private String message;
}
