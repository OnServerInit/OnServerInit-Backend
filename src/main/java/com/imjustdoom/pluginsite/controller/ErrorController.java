package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.model.Account;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ErrorController {
    /* idk what an Exception error is */
//    @ExceptionHandler(Exception.class)
//    public String handleException() {
//        return "error/error";
//    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(Model model, Account account) {
        model.addAttribute("account", account);
        return "error/404";
    }
}
