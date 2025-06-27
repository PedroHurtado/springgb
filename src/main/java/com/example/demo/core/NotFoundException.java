package com.example.demo.core;

public class NotFoundException extends RuntimeException {
    public NotFoundException(){
        super();
    }
    public NotFoundException(String message){
        super(message);
    }

}
