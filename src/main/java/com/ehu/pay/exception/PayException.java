package com.ehu.pay.exception;

/**
 * 
 * @author AlanSun
 * 2016-08-10
 *
 */
public class PayException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5328505822127772820L;
	private int errorcode;
	private String errormsg;

	public PayException() {
	}

	public PayException(Exception e) {
		super(e);
	}
	
	public PayException(int errorcode, String msg) {
		super(msg);
		this.errorcode = errorcode;
		this.errormsg = msg;
	}
	
	public PayException(int errorcode, String msg, Throwable cause) {
		super(msg, cause);
		this.errorcode = errorcode;
		this.errormsg = msg;
	}

	
	public int getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(int errorcode) {
		this.errorcode = errorcode;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
}