package com.ehu.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author alan
 * @createtime 18-8-28 下午1:18 *
 */
@Getter
@Setter
@NoArgsConstructor
public class PayResponse<T> {
    private Boolean result;
    private String resultCode;
    private String resultMessage;
    private T data;

    public PayResponse(T data) {
        this.data = data;
    }
}
