package com.inf.medical_record_system.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Component
public class CustomErrorViewResolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(
            HttpServletRequest request,
            HttpStatus status,
            Map<String, Object> model
    ) {
        if (status == HttpStatus.NOT_FOUND) {
            return new ModelAndView("errors/404", model, status);
        }

        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ModelAndView("errors/500", model, status);
        }

        return new ModelAndView("errors/500", model, status);
    }
}