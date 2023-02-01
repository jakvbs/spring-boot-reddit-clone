package pl.jsieczczynski.SpringBootRedditClone.controller.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.AppException;

public abstract class BaseController {
    @ExceptionHandler({AppException.class})
    public String handleInvalidRequestException(AppException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error/400";
    }

    @ExceptionHandler({Exception.class})
    public String handleException(Exception e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error/500";
    }
}
