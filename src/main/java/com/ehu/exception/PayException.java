package com.ehu.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * 2016-08-10
 */
@Getter
@Setter
public class PayException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -5328505822127772820L;
    private int errorcode;
    private String errormsg;

    public PayException() {
    }

    public PayException(int errorcode, String msg) {
        super(msg);
        this.errorcode = errorcode;
        this.errormsg = msg;
    }

    public PayException(String msg) {
        super(msg);
        this.errormsg = msg;
    }
}