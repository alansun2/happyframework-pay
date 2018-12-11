package com.ehu.bean;

import lombok.Data;
import org.apache.http.message.BasicHeader;

@Data
public class HttpParams {
    private String url;
    private BasicHeader[] headers;
    private String strEntity;
}
