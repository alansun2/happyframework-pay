package com.ehu.bean;

import lombok.*;

/**
 * @author alan
 * @createtime 18-8-28 下午1:18 *
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayResponse<T> {
    private String resultCode = "SUCCESS";
    private String resultMessage;
    private T data;

    public PayResponse(T data) {
        this.data = data;
    }
}
