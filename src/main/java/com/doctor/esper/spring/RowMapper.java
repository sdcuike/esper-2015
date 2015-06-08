package com.doctor.esper.spring;

import com.espertech.esper.client.EventBean;

/**
 * @author doctor
 * 
 * @see org.opencredo.esper.ParameterizedEsperRowMapper
 * 
 * @param <T>
 *
 * @time 2015年6月8日 下午4:53:28
 */
public interface RowMapper<T> {

	T mapRow(EventBean eventBean);
}
