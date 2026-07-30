package org.example.CampusMart.common.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.example.CampusMart.common.result.Result;
import org.example.CampusMart.common.result.ResultCodeEnum;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 @RequestBody + @Valid 参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.fail(ResultCodeEnum.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理 @RequestParam / @PathVariable + @Validated 参数校验失败（单个参数）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return Result.fail(ResultCodeEnum.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(CampusMartException.class)
    @ResponseBody
    public Result handle(CampusMartException e){
        e.printStackTrace();
        return Result.fail(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handle(Exception e){
        e.printStackTrace();
        return Result.fail();
    }
}
