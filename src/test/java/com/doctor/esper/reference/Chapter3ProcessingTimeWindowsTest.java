package com.doctor.esper.reference;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doctor.esper.common.CommonUpdateListener;
import com.doctor.esper.common.EsperUtil;
import com.doctor.esper.event.Withdrawal;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;

/**
 * Chapter 3 Processing Time Windows
 * 
 * @author doctor
 *
 * @time 2015年6月2日 下午2:20:38
 */
public class Chapter3ProcessingTimeWindowsTest {
	private static final Logger log = LoggerFactory.getLogger(Chapter3ProcessingTimeWindowsTest.class);
	private static final String config = "esper2015Config/esper-2015.esper.cfg.xml";

	private EPServiceProvider epServiceProvider;

	@Before
	public void init() {
		epServiceProvider = EsperUtil.esperConfig(config);
	}

	/**
	 * 3.7.1. Insert and Remove Stream
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_3_7_1_Insert_and_Remove_Stream() throws InterruptedException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("select count(*) as myCount ")
				.append("from Withdrawal ")
				.append("having count(*)= 2");

		String epl = stringBuilder.toString();
		EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(epl);
		epStatement.addListener(CommonUpdateListener::update);

		Withdrawal withdrawal = new Withdrawal("doctor", BigDecimal.valueOf(123.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		withdrawal = new Withdrawal("doctor who ", BigDecimal.valueOf(23.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		withdrawal = new Withdrawal("doctor who 2", BigDecimal.valueOf(23.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

	}

}
