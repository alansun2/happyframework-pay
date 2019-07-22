package com.ehu.core.httpresponsehandler;

import org.apache.http.impl.client.AbstractResponseHandler;

import java.util.Map;

/**
 * @author AlanSun
 * @date 2019/7/1 10:33
 **/
abstract class MapResponseHandlerAbstract<T> extends AbstractResponseHandler<Map<String, T>> {
}
