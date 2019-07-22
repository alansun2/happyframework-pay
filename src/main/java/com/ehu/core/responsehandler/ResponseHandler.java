package com.ehu.core.responsehandler;

import com.ehu.bean.PayResponse;
import com.ehu.exception.PayException;

/**
 * @author AlanSun
 * @date 2019/7/5 16:02
 **/
public interface ResponseHandler<T, P, R> {

    PayResponse<R> handler(T response, P params) throws PayException;
}
