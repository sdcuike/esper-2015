package com.doctor.esper.spring;

import java.util.List;

/**
 * @author doctor
 *
 * @time 2015年6月8日 下午5:48:22
 */
public class EsperQueryStatement implements OnDemandFireAndForgetQuery {
	private String epl;

	public EsperQueryStatement(String epl) {
		this.epl = epl;
	}

	public String getEpl() {
		return epl;
	}

	@Override
	public <T> List<T> executeQuery(RowMapper<T> rm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> prepareQuery(RowMapper<T> rm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> prepareQueryWithParameters(RowMapper<T> rm, Object... parameter) {
		// TODO Auto-generated method stub
		return null;
	}

}
