package com.doctor.esper.reference;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doctor.esper.common.EsperUtil;
import com.doctor.esper.common.MyUpdateListener;
import com.doctor.esper.event.Withdrawal;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;

/**
 * 3.5. Chapter 3. Processing Model -> Time Windows
 * 
 * @author doctor
 *
 * @time 2015年6月1日 下午4:58:33
 */
public class Chapter3ProcessingTimeWindows {
	private static final String config = "esper2015Config/esper-2015.esper.cfg.xml";

	private static final Logger log = LoggerFactory.getLogger(Chapter3ProcessingTimeWindows.class);

	public static void main(String[] args) throws InterruptedException {
		EPServiceProvider epServiceProvider = EsperUtil.esperConfig(config);

		log.info("{info:'3.5.1. Time Window'}");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("select * ")
				.append("from Withdrawal.win:time(4 sec)");

		String epl = stringBuilder.toString();
		EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(epl);
		epStatement.addListener(MyUpdateListener::update);

		Withdrawal withdrawal = new Withdrawal("doctor", BigDecimal.valueOf(123.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		TimeUnit.SECONDS.sleep(4);

		withdrawal = new Withdrawal("doctor who ", BigDecimal.valueOf(23.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		TimeUnit.SECONDS.sleep(3);

		// 输出结果：时间窗口数据结构（只保留最近时间段数据）和长度窗口数据结构类似，度量标准不同而已。从官方图示可知，时间窗口持续移动者。
		// {newEvents:BeanEventBean eventType=BeanEventType name=Withdrawal clazz=com.doctor.esper.event.Withdrawal bean={"account":"doctor","amount":123.5}}
		// {oldEvents:BeanEventBean eventType=BeanEventType name=Withdrawal clazz=com.doctor.esper.event.Withdrawal bean={"account":"doctor","amount":123.5}}
		// {newEvents:BeanEventBean eventType=BeanEventType name=Withdrawal clazz=com.doctor.esper.event.Withdrawal bean={"account":"doctor who ","amount":23.5}}
		log.info("{info:'3.5.1.  Time Batch'}");
		stringBuilder = new StringBuilder();
		stringBuilder.append("select * ")
				.append("from Withdrawal.win:time_batch(4 sec)");

		epl = stringBuilder.toString();
		epStatement.removeAllListeners();
		epStatement.addListener(MyUpdateListener::update);
		TimeUnit.SECONDS.sleep(1);
		withdrawal = new Withdrawal("doctor", BigDecimal.valueOf(123.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		TimeUnit.SECONDS.sleep(3);
		withdrawal = new Withdrawal("doctor who ", BigDecimal.valueOf(23.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);

		TimeUnit.SECONDS.sleep(7);
		withdrawal = new Withdrawal("doctor who ", BigDecimal.valueOf(23.50D));
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		// 从输出结果来看：时间批处理窗口函数会每隔一个时间窗口期，批量处理事件流的进入或淘汰（触发监听器）。从官方文档图示所示，窗口是每隔一段时间移动一个时间窗口。
		// {oldEvents:BeanEventBean eventType=BeanEventType name=Withdrawal clazz=com.doctor.esper.event.Withdrawal bean={"account":"doctor who ","amount":23.5}}
		// {newEvents:BeanEventBean eventType=BeanEventType name=Withdrawal clazz=com.doctor.esper.event.Withdrawal bean={"account":"doctor","amount":123.5}}
		// {newEvents:BeanEventBean eventType=BeanEventType name=Withdrawal clazz=com.doctor.esper.event.Withdrawal bean={"account":"doctor who ","amount":23.5}}
		// {oldEvents:BeanEventBean eventType=BeanEventType name=Withdrawal clazz=com.doctor.esper.event.Withdrawal bean={"account":"doctor","amount":123.5}}
		// {oldEvents:BeanEventBean eventType=BeanEventType name=Withdrawal clazz=com.doctor.esper.event.Withdrawal bean={"account":"doctor who ","amount":23.5}}
		// {newEvents:BeanEventBean eventType=BeanEventType name=Withdrawal clazz=com.doctor.esper.event.Withdrawal bean={"account":"doctor who ","amount":23.5}}

	}

}
