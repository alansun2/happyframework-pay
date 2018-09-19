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
public class PayResponse<T extends Object> {
    private T result;
    private String resultCode;
    private String resultMessage;

    public PayResponse(T result){
        this.result = result;
    }
}
