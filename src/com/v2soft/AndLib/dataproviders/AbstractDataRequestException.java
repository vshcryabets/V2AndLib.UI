package com.v2soft.AndLib.dataproviders;

/**
 * Data request exception
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public class AbstractDataRequestException extends Exception {
	private static final long serialVersionUID = 1L;

	public AbstractDataRequestException(String message) {
		super(message);
	}
	
	public AbstractDataRequestException(Throwable e) {
		super(e);
	}
}

