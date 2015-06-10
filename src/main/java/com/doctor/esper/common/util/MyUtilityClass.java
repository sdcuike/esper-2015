package com.doctor.esper.common.util;

/**
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#custom-singlerow-function
 * 
 *      18.3.1. Implementing a Single-Row Function
 * 
 * @author doctor
 *
 * @time 2015年6月10日 下午4:06:46
 */
public final class MyUtilityClass {

	/**
	 * @see java.lang.Math.incrementExact(int)
	 * @param a
	 * @return
	 */
	public static int incrementExact(int a) {
		if (a == Integer.MAX_VALUE) {
			throw new ArithmeticException("integer overflow");
		}
		return a + 1;
	}
}
