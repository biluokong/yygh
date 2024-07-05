package com.biluo.yygh.common.exception;

import com.biluo.yygh.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public Result error(Exception e) {
		log.error("全局异常处理器：{}", e.getMessage(), e);
		return Result.fail();
	}

	@ExceptionHandler(YyghException.class)
	public Result error(YyghException e) {
		log.error("全局异常处理器：{}", e.getMessage(), e);
		return Result.build(e.getCode(), e.getMessage());
	}
}
