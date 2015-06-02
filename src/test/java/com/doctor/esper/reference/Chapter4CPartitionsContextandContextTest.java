package com.doctor.esper.reference;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.doctor.esper.common.CommonUpdateListener;
import com.doctor.esper.common.EsperUtil;
import com.doctor.esper.event.Withdrawal;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;

/**
 * Chapter 4.Partitions Contextand Context
 * 
 * @author doctor
 *
 * @time 2015年6月2日 下午4:03:38
 */
public class Chapter4CPartitionsContextandContextTest {
	private static final Logger log = LoggerFactory.getLogger(Chapter4CPartitionsContextandContextTest.class);
	private static final String config = "esper2015Config/esper-2015.esper.cfg.xml";

	private EPServiceProvider epServiceProvider;

	@Before
	public void init() {
		epServiceProvider = EsperUtil.esperConfig(config);
	}

	@Test
	public void test() throws InterruptedException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("select account,amount  ")
				.append("from Withdrawal.win:time(5.5 sec) ");

		String epl = stringBuilder.toString();
		EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(epl);
		epStatement.addListener(CommonUpdateListener::update);

		Withdrawal withdrawal = new Withdrawal("doctor", BigDecimal.valueOf(123.50D));
		TimeUnit.SECONDS.sleep(2);
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		List<EventBean> list = EsperUtil.get(epStatement);
		log.info("{list:'{}'}", JSON.toJSONString(list));
	}
}
