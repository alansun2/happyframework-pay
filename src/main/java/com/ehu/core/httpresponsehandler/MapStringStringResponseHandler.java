package com.ehu.core.httpresponsehandler;

import com.alan344happyframework.util.XmlUtils;
import org.apache.http.HttpEntity;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author AlanSun
 * @date 2019/7/1 10:34
 **/
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class MapStringStringResponseHandler extends MapResponseHandlerAbstract<String> {
    @Override
    public Map<String, String> handleEntity(HttpEntity entity) throws IOException {
        return XmlUtils.xmlToMap(EntityUtils.toString(entity));
    }
}
