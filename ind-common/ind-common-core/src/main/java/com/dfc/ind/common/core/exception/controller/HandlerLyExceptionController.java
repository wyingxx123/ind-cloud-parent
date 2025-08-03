package com.dfc.ind.common.core.exception.controller;

import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.web.domain.JsonResults;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class HandlerLyExceptionController {

/*
     * 表示当前处理器会自动拦截Exception异常
     * @param e
      @return*/

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonResults> handlerLyException(Exception e){
        log.error("系统异常:",e);
        if (e instanceof CustomException){
            return ResponseEntity.status(500).body(JsonResults.error(e.getMessage()));
        }
        if (e instanceof NullPointerException){
            return ResponseEntity.status(500).body(JsonResults.error("查询不到数据,请联系管理员"));
        }
        if (e instanceof TooManyResultsException){
            return ResponseEntity.status(500).body(JsonResults.error("数据重复异常,请联系管理员"));
        }
        if (e instanceof RuntimeException){
            return ResponseEntity.status(500).body(JsonResults.error(e.getMessage()));
        }
        return ResponseEntity.status(500).body(JsonResults.error("执行失败,请联系管理员"));
    }
}
