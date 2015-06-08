package com.doctor.esper.spring;

import java.util.List;

/**
 * @see org.opencredo.esper.EsperStatementOperations
 * 
 * @author doctor
 *
 * @time 2015年6月8日 下午4:45:57
 */
public interface EsperStatementOperation {

	<T> T concurrentSafeQueryForObject(RowMapper<T> rm);

	<T> List<T> concurrentSafeQuery(RowMapper<T> rm);

	<T> T concurrentUnsafeQueryForObject(RowMapper<T> rm);

	<T> List<T> concurrentUnsafeQuery(RowMapper<T> rm);

}
