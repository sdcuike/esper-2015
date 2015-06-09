package com.doctor.esper.spring;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.espertech.esper.client.EPOnDemandPreparedQuery;
import com.espertech.esper.client.EPOnDemandPreparedQueryParameterized;
import com.espertech.esper.client.EPOnDemandQueryResult;

/**
 * On-Demand Fire-And-Forget Query Execution
 * 
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#api-ondemand
 * 
 * @author doctor
 *
 * @time 2015年6月8日 下午5:48:22
 */
public class EsperQueryStatement implements OnDemandFireAndForgetQuery {
	private EsperTemplateBean esperTemplateBean;

	private String epl;

	public EsperQueryStatement() {

	}

	public EsperQueryStatement(EsperTemplateBean esperTemplateBean, String epl) {
		this.esperTemplateBean = esperTemplateBean;
		this.epl = epl;
	}

	public void setEsperTemplateBean(EsperTemplateBean esperTemplateBean) {
		this.esperTemplateBean = esperTemplateBean;
	}

	public void setEpl(String epl) {
		this.epl = epl;
	}

	@Override
	public <T> List<T> executeQuery(RowMapper<T> rm) {
		EPOnDemandQueryResult result = esperTemplateBean.getEsperNativeRuntime().executeQuery(epl);

		return Stream.of(result.getArray()).map(eventBean -> (T) rm.mapRow(eventBean)).collect(Collectors.toList());
	}

	@Override
	public <T> List<T> prepareQuery(RowMapper<T> rm) {
		EPOnDemandPreparedQuery prepareQuery = esperTemplateBean.getEsperNativeRuntime().prepareQuery(epl);
		EPOnDemandQueryResult result = prepareQuery.execute();
		return Stream.of(result.getArray()).map(eventBean -> (T) rm.mapRow(eventBean)).collect(Collectors.toList());
	}

	@Override
	public <T> List<T> prepareQueryWithParameters(RowMapper<T> rm, Object... parameter) {
		EPOnDemandPreparedQueryParameterized queryWithParameters = esperTemplateBean.getEsperNativeRuntime().prepareQueryWithParameters(epl);
		if (parameter != null) {
			for (int i = 0, length = parameter.length; i < length; i++) {
				queryWithParameters.setObject(i + 1, parameter[i]);
			}
		}

		EPOnDemandQueryResult result = esperTemplateBean.getEsperNativeRuntime().executeQuery(queryWithParameters);

		return Stream.of(result.getArray()).map(eventBean -> (T) rm.mapRow(eventBean)).collect(Collectors.toList());
	}

}
