package com.luiz.frauddetection.config.exception;

public class ConflictException extends RuntimeException{

    public ConflictException(String mensagem){
        super(mensagem);
    }
}
