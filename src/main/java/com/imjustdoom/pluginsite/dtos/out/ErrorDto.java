package com.imjustdoom.pluginsite.dtos.out;

public record ErrorDto(int error, String errorText) {

    public static ErrorDto create(int error, String errorText) {
        return new ErrorDto(error, errorText);
    }
}
