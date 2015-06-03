package com.doctor.esper.reference;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doctor.esper.common.EsperUtil;
import com.doctor.esper.event.Withdrawal;
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
			log.info(withdrawal.toString());
		}
	}
}
