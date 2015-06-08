package com.doctor.esper.spring;

/**
 * @author doctor
 * 
 * @see org.opencredo.esper.InvalidEsperConfigurationException;
 * 
 * @time 2015年6月8日 下午5:06:58
 */
public class InvalidEsperConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidEsperConfigurationException(String message) {
		super(message);
	}

	public InvalidEsperConfigurationException(String message, Throwable originalException) {
		super(message, originalException);
	}
}
