package com.alan344happyframework.core;

import com.alibaba.fastjson.serializer.NameFilter;
import com.google.common.base.CaseFormat;

/**
 * @author alan
 * @createtime 18-8-28 上午10:59 *
 */
public class LowerUnderscoreFilter implements NameFilter {
    /**
     * fastjson 将key转为下划线
     *
     * @param object
     * @param name
     * @param value
     * @return
     */
    @Override
    public String process(Object object, String name, Object value) {
        if (name == null || name.length() == 0) {
            return name;
        }

        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }
}
