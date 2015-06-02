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
		List<EventBean> list = EsperUtil.get(epStatement);
		log.info("{list:'{}'}", list);

		withdrawal = new Withdrawal("doctor who ", BigDecimal.valueOf(23.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		withdrawal = new Withdrawal("doctor who 2", BigDecimal.valueOf(23.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		list = EsperUtil.get(epStatement);
		log.info("{list:'{}'}", list);
	}

	/**
	 * A.1. Introduction and Sample Data
	 * 选用粒度5.5s间隔作为时间窗口，来演示失效策略事件流的进入和流出。
	 * 
	 * 查看输出内容和文档：事件数据集只保存最近5.5s时间窗口（FIFO缓存）。不在这个窗口期的事件过期。
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_A_1_Introduction_and_Sample_Data() throws InterruptedException {
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

		withdrawal = new Withdrawal("doctor who ", BigDecimal.valueOf(23.50D));
		TimeUnit.SECONDS.sleep(4);
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		withdrawal = new Withdrawal("doctor who 2", BigDecimal.valueOf(23.50D));
		TimeUnit.SECONDS.sleep(7);
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		withdrawal = new Withdrawal("doctor who 3", BigDecimal.valueOf(123.50D));
		withdrawal = new Withdrawal("doctor who 5", BigDecimal.valueOf(223.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		list = EsperUtil.get(epStatement);
		log.info("{list:'{}'}", JSON.toJSONString(list));

	}

	/**
	 * A.2. Output for Un-aggregated and Un-grouped Queries
	 * A.2.2. Output Rate Limiting - Default
	 * 
	 * With an output clause, the engine dispatches to listeners when the output condition occurs.
	 * Here, the output condition is a 1-second time interval.
	 * The engine thus outputs every 1 second, starting from the first event,
	 * 
	 * even if there are no new events or no expiring events to output.
	 * 
	 * The default (no keyword) and the ALL keyword result in the same output.
	 * 
	 * 我们可以用output语句，让引擎在output 条件触发时候，触发监听器。
	 * 这里，我们设置output条件为1秒时间间隔。引擎每当1秒间隔，就会把从第一个事件开始传递并触发监听器，
	 * 即使没有新事件或者过去事件要触发也会执行该操作。
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_A_2_2_Output_Rate_Limiting_Default() throws InterruptedException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("select account,amount  ")
				.append("from Withdrawal.win:time(5.5 sec) ")
				.append("output every 1 seconds");

		String epl = stringBuilder.toString();
		EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(epl);
		epStatement.addListener(CommonUpdateListener::update);

		Withdrawal withdrawal = new Withdrawal("doctor", BigDecimal.valueOf(123.50D));
		TimeUnit.SECONDS.sleep(2);
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		List<EventBean> list = EsperUtil.get(epStatement);
		log.info("{list:'{}'}", JSON.toJSONString(list));

		withdrawal = new Withdrawal("doctor who ", BigDecimal.valueOf(23.50D));
		TimeUnit.SECONDS.sleep(4);
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		withdrawal = new Withdrawal("doctor who 2", BigDecimal.valueOf(23.50D));
		TimeUnit.SECONDS.sleep(7);
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		withdrawal = new Withdrawal("doctor who 3", BigDecimal.valueOf(123.50D));
		withdrawal = new Withdrawal("doctor who 5", BigDecimal.valueOf(223.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		list = EsperUtil.get(epStatement);
		log.info("{list:'{}'}", JSON.toJSONString(list));
	}
}
