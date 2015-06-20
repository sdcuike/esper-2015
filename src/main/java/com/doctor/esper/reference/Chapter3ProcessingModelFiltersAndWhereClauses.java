package com.doctor.esper.reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doctor.esper.common.EsperUtil;
import com.doctor.esper.tutorial.OrderEvent;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.soda.StreamSelector;

/**
 * Chapter 3. Processing Model Filters and Where-clauses
 * 
 * 
 * 1.esper处理模型是持续的，像jstorm流一样。esper引擎接收到事件流，相应Statement的更新监听器和订阅者会收到更新的数据
 * （它门的处理和Statement所选择的事件流、视图、过滤器和输出速率有关系).
 * 
 * @author doctor
 *
 * @time 2015年6月1日 下午2:11:12
 */
public class Chapter3ProcessingModelFiltersAndWhereClauses {
	private static final Logger log = LoggerFactory.getLogger(Chapter3ProcessingModelFiltersAndWhereClauses.class);

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		configuration.addEventTypeAutoName("com.doctor.esper.tutorial");

		// 设置事件更新输出内容。
		configuration.getEngineDefaults().getStreamSelection().setDefaultStreamSelector(StreamSelector.RSTREAM_ISTREAM_BOTH);

		EPServiceProvider epServiceProvider = EPServiceProviderManager.getDefaultProvider(configuration);

		// 3.2. Insert Stream
		// 下面的EPStatement选择存储所有OrderEvent。每当esper引擎处理OrderEvent事件流或者OrderEvent子类型的事件流，
		// epser引擎会触发该EPStatement的监听器。
		log.info("{msg:'3.2. Insert Stream'}");
		String expression = "select * from OrderEvent";
		EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(expression);

		epStatement.addListener(Chapter3ProcessingModelFiltersAndWhereClauses::update);

		OrderEvent orderEvent = new OrderEvent("shirt", 75.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);

		orderEvent = new OrderEvent("aaa", 35.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);

		orderEvent = new OrderEvent("bbb", 85.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);
		log.info("{list:{}}", EsperUtil.get(epStatement));
		// 输出结果：每当事件流流向EPStatement存储结构时候，都会触发更新监听器。newEvents就代表新进来的数据
		// 输出的list为空，知道为什么吗？因为没定义存储结构，像数据窗口这样的结构。
		// 06-01 14:43:01.648 main INFO c.d.e.r.Chapter3ProcessingModel - {newEvents:BeanEventBean eventType=BeanEventType name=OrderEvent
		// clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"shirt","price":75.5}}

		// 06-01 14:43:01.744 main INFO c.d.e.r.Chapter3ProcessingModel - {newEvents:BeanEventBean eventType=BeanEventType name=OrderEvent
		// clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"aaa","price":35.5}}

		// 06-01 14:43:01.745 main INFO c.d.e.r.Chapter3ProcessingModel - {newEvents:BeanEventBean eventType=BeanEventType name=OrderEvent
		// clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"bbb","price":85.5}}
		// {list:[]}
		// 3.3. Insert and Remove Stream 容量限制窗口，像FIFO缓存，只保存事件流中最后N个事件
		log.info("{msg:'3.3. Insert and Remove Stream'}");
		epServiceProvider.getEPAdministrator().destroyAllStatements();
		epServiceProvider.removeAllStatementStateListeners();
		expression = "select   * from OrderEvent.win:length(1)";
		epStatement = epServiceProvider.getEPAdministrator().createEPL(expression);
		epStatement.addListener(Chapter3ProcessingModelFiltersAndWhereClauses::update);
		orderEvent = new OrderEvent("1", 75.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);

		orderEvent = new OrderEvent("2", 35.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);

		orderEvent = new OrderEvent("3", 85.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);
		orderEvent = new OrderEvent("4", 85.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);
		log.info("{list:{}}", EsperUtil.get(epStatement));
		// 输出结果：EPStatement限制存储容量为2.事件体现了进、出容量的变化。newEvents表示新事件出现，oldEvents表示EPStatement
		// 容量限制丢弃的老事件。
		// 06-01 15:22:20.551 main INFO c.d.e.r.Chapter3ProcessingModel - {newEvents:BeanEventBean eventType=BeanEventType name=OrderEvent
		// clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"1","price":75.5}}
		// 06-01 15:22:20.551 main INFO c.d.e.r.Chapter3ProcessingModel - {newEvents:BeanEventBean eventType=BeanEventType name=OrderEvent
		// clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"2","price":35.5}}
		// 06-01 15:22:20.551 main INFO c.d.e.r.Chapter3ProcessingModel - {oldEvents:BeanEventBean eventType=BeanEventType name=OrderEvent
		// clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"1","price":75.5}}
		// 06-01 15:22:20.551 main INFO c.d.e.r.Chapter3ProcessingModel - {newEvents:BeanEventBean eventType=BeanEventType name=OrderEvent
		// clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"3","price":85.5}}
		// 06-01 15:22:20.551 main INFO c.d.e.r.Chapter3ProcessingModel - {oldEvents:BeanEventBean eventType=BeanEventType name=OrderEvent
		// clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"2","price":35.5}}
		// 06-01 15:22:20.551 main INFO c.d.e.r.Chapter3ProcessingModel - {newEvents:BeanEventBean eventType=BeanEventType name=OrderEvent
		// clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"4","price":85.5}}
		// 06-01 15:22:20.552 main INFO c.d.e.r.Chapter3ProcessingModel - {oldEvents:BeanEventBean eventType=BeanEventType name=OrderEvent
		// clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"3","price":85.5}}

		// {list:[BeanEventBean eventType=BeanEventType name=OrderEvent clazz=com.doctor.esper.tutorial.OrderEvent
		// bean={"itemName":"4","price":85.5}]}

		// 3.4. Filters and Where-clauses 事件流过滤器允许什么样的事件流向数据窗口。
		// Filters=》只有符合事件流过滤器的事件才能真正的进入数据窗口并且触发相应的监听器
		// Where-clauses 和have 语句 ==》事件流进入数据窗口或视图后的结果查询，并不影响事件流进入数据窗口。

		log.info("{msg:'3.4. Filters and Where-clauses'}");
		epServiceProvider.getEPAdministrator().destroyAllStatements();
		epServiceProvider.removeAllStatementStateListeners();
		expression = "select   * from OrderEvent(price > 50 ).win:length(3)";
		epStatement = epServiceProvider.getEPAdministrator().createEPL(expression);
		epStatement.addListener(Chapter3ProcessingModelFiltersAndWhereClauses::update);
		orderEvent = new OrderEvent("1", 75.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);

		orderEvent = new OrderEvent("2", 35.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);

		orderEvent = new OrderEvent("3", 85.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);
		orderEvent = new OrderEvent("4", 5.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);
		log.info("{list:{}}", EsperUtil.get(epStatement));

		// {newEvents:BeanEventBean eventType=BeanEventType name=OrderEvent clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"1","price":75.5}}
		// {newEvents:BeanEventBean eventType=BeanEventType name=OrderEvent clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"3","price":85.5}}
		// {list:[BeanEventBean eventType=BeanEventType name=OrderEvent clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"1","price":75.5}, BeanEventBean eventType=BeanEventType name=OrderEvent clazz=com.doctor.esper.tutorial.OrderEvent bean={"itemName":"3","price":85.5}]}

		log.info("{msg:'3.4. Where-clauses'}");
		epServiceProvider.getEPAdministrator().destroyAllStatements();
		epServiceProvider.removeAllStatementStateListeners();
		expression = "select   * from OrderEvent.win:length(3) where price > 50 ";
		epStatement = epServiceProvider.getEPAdministrator().createEPL(expression);
		epStatement.addListener(Chapter3ProcessingModelFiltersAndWhereClauses::update);
		orderEvent = new OrderEvent("1", 75.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);

		orderEvent = new OrderEvent("2", 35.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);

		orderEvent = new OrderEvent("3", 85.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);
		orderEvent = new OrderEvent("4", 5.50D);

		epServiceProvider.getEPRuntime().sendEvent(orderEvent);
		log.info("{list:{}}", EsperUtil.get(epStatement));

	}

	public static void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null && newEvents[0] != null) {
			log.info("{newEvents:{}}", newEvents[0]);
		}
		if (oldEvents != null && oldEvents[0] != null) {
			log.info("{oldEvents:{}}", oldEvents[0]);
		}
	}
}
