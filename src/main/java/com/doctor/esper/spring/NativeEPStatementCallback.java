package com.doctor.esper.spring;

import com.espertech.esper.client.EPStatement;

/**
 * @see org.opencredo.esper.NativeEPStatementCallback
 * 
 * @author doctor
 *
 * @time 2015年6月8日 下午5:20:31
 */
public interface NativeEPStatementCallback {
	/**
	 * Passes the native EPStatement and registered EPL filter query to the
	 * implementor.
	 * 
	 * @param nativeStatement
	 *            the native EPStatement
	 * @param epl
	 *            the epl filter query registered with this statement
	 */
	public void executeWithEPStatement(EPStatement nativeStatement, String epl);
}
