package com.doctor.esper.reference;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.doctor.esper.common.EsperUtil;
import com.doctor.esper.event.Withdrawal;
import com.espertech.esper.client.EPOnDemandPreparedQuery;
import com.espertech.esper.client.EPOnDemandPreparedQueryParameterized;
import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;

/**
 * Chapter 15. API Reference
 * 
 * @author doctor
 *
 * @time 2015年6月3日 上午11:04:33
 */
public class Chapter15APIReferenceTest {
	private static final Logger log = LoggerFactory.getLogger(Chapter15APIReferenceTest.class);
	private static final String config = "esper2015Config/esper-2015.esper.cfg.xml";

	private EPServiceProvider epServiceProvider;

	@Before
	public void init() {
		epServiceProvider = EsperUtil.esperConfig(config);
	}

	@After
	public void close() {
		epServiceProvider.destroy();
	}

	/**
	 * 15.3.3. Setting a Subscriber Object
	 */
	@Test
	public void test_Setting_a_Subscriber_Object() throws Throwable {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("select *  ")
				.append("from Withdrawal");

		String epl = stringBuilder.toString();
		EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(epl);
		epStatement.setSubscriber(new WithdrawalSubscriber());

		Withdrawal withdrawal = new Withdrawal("doctor", BigDecimal.valueOf(123.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		withdrawal = new Withdrawal("doctor who", BigDecimal.valueOf(123.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		// If your select clause contains one or more wildcards (*), then the equivalent parameter type is the underlying event type of the stream selected from.
		epServiceProvider.initialize();
		stringBuilder = new StringBuilder();
		stringBuilder.append("select * ,count(*) ")
				.append("from Withdrawal");

		epl = stringBuilder.toString();
		epStatement = epServiceProvider.getEPAdministrator().createEPL(epl);
		epStatement.setSubscriber(new WithdrawalSubscriber(), "update2");

		withdrawal = new Withdrawal("doctor", BigDecimal.valueOf(123.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		withdrawal = new Withdrawal("doctor who", BigDecimal.valueOf(123.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		//
		// 15.3.3.1.2. Row Delivery as Map and Object Array
		epServiceProvider.initialize();
		stringBuilder = new StringBuilder();
		stringBuilder.append("select * ,count(*) ")
				.append("from Withdrawal");

		epl = stringBuilder.toString();
		epStatement = epServiceProvider.getEPAdministrator().createEPL(epl);
		epStatement.setSubscriber(new WithdrawalSubscriber(), "updateUseMap");

		withdrawal = new Withdrawal("doctor", BigDecimal.valueOf(123.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		withdrawal = new Withdrawal("doctor who", BigDecimal.valueOf(123.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		log.info("{list:'{}'}", EsperUtil.get(epStatement));

		//
	}

	/**
	 * 15.5. On-Demand Fire-And-Forget Query Execution
	 */
	@Test
	public void test_On_Demand_Fire_And_Forget_Query_Execution() {
		StringBuilder window = new StringBuilder();
		window.append("create window WithdrawalWindow.win:length(3)")
				.append("select * from Withdrawal");
		epServiceProvider.getEPAdministrator().createEPL(window.toString());
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(" insert into WithdrawalWindow select *  ")
				.append("from Withdrawal.win:length(3)");

		String epl = stringBuilder.toString();
		EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(epl);
		epStatement.setSubscriber(new WithdrawalSubscriber());

		Withdrawal withdrawal = new Withdrawal("doctor", BigDecimal.valueOf(23.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		withdrawal = new Withdrawal("doctor who", BigDecimal.valueOf(123.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		withdrawal = new Withdrawal("doctor who 2", BigDecimal.valueOf(223.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		withdrawal = new Withdrawal("doctor who 6", BigDecimal.valueOf(323.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		// 15.5.1. On-Demand Query Single Execution
		System.out.println("15.5.1. On-Demand Query Single Execution");
		String query = "select * from WithdrawalWindow ";
		EPOnDemandQueryResult queryResult = epServiceProvider.getEPRuntime().executeQuery(query);
		System.out.println(queryResult.getArray().length);
		Stream.of(queryResult.getArray()).forEach(System.out::println);

		// 15.5.2. On-Demand Query Prepared Unparameterized Execution
		System.out.println("15.5.2. On-Demand Query Prepared Unparameterized Execution");

		EPOnDemandPreparedQuery preparedQuery = epServiceProvider.getEPRuntime().prepareQuery(query);
		EPOnDemandQueryResult result = preparedQuery.execute();
		System.out.println(result.getArray().length);
		Stream.of(result.getArray()).forEach(System.out::println);

		// 15.5.3. On-Demand Query Prepared Parameterized Execution
		System.out.println("15.5.3. On-Demand Query Prepared Parameterized Execution");
		query = "select * from WithdrawalWindow  where amount > ?";
		EPOnDemandPreparedQueryParameterized queryParameterized = epServiceProvider.getEPRuntime().prepareQueryWithParameters(query);
		queryParameterized.setObject(1, 200);
		result = epServiceProvider.getEPRuntime().executeQuery(queryParameterized);
		System.out.println(result.getArray().length);
		Stream.of(result.getArray()).forEach(System.out::println);

		queryParameterized.setObject(1, 300);
		result = epServiceProvider.getEPRuntime().executeQuery(queryParameterized);
		System.out.println(result.getArray().length);
		Stream.of(result.getArray()).forEach(System.out::println);
	}

	/**
	 * The best delivery performance can generally be achieved by attaching a subscriber and by not attaching listeners.
	 * 官方建议用subscriber，不建议用listeners。如果两者都存在，subscriber先触发而listeners后触发。
	 * 如果有多个listeners，则按照它们添加的顺序触发。
	 * 但是一个EPStatement只有0个或1个subscriber。如果要多个，请使用listeners（虽然性能不佳，没办法）。
	 * 
	 * @author doctor
	 *
	 * @time 2015年6月3日 上午11:15:53
	 */
	public static class WithdrawalSubscriber {
		public void update(Withdrawal withdrawal) {
			log.info("{new:'{}'}", withdrawal);
		}

		public void updateRStream(Withdrawal withdrawal) {
			log.info("{old:'{}'}", withdrawal);
		}

		public void update2(Withdrawal withdrawal, long count) {
			log.info("{withdrawal:'{}',count:'{}'}", withdrawal, count);
		}

		public void updateUseMap(Map<String, Object> map) {
			log.info(JSON.toJSONString(map));
		}
	}
}
