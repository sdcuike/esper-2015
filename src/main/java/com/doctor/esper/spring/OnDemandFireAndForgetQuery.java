package com.doctor.esper.spring;

import java.util.List;

/**
 * @author doctor
 *
 * @time 2015年6月8日 下午5:27:05
 */
public interface OnDemandFireAndForgetQuery {

	public <T> List<T> executeQuery(RowMapper<T> rm);

	public <T> List<T> prepareQuery(RowMapper<T> rm);

	public <T> List<T> prepareQueryWithParameters(RowMapper<T> rm, Object... parameter);
}
