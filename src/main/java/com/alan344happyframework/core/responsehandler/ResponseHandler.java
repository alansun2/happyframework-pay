package com.alan344happyframework.core.responsehandler;

import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.exception.PayException;

/**
 * @author AlanSun
 * @date 2019/7/5 16:02
 **/
public interface ResponseHandler<T, P, R> {

    PayResponse<R> handler(T response, P params) throws PayException;
}
