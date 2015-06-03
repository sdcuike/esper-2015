package com.doctor.esper.reference;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doctor.esper.common.CommonUpdateListener;
import com.doctor.esper.common.EsperUtil;
import com.doctor.esper.event.Person;
import com.doctor.esper.event.Withdrawal;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;

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

	@After
	public void close() {
		epServiceProvider.destroy();
	}

	@Test
	public void test() throws InterruptedException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("select account,amount  ")
				.append("from Withdrawal.win:time(2.5 sec) ");

		String epl = stringBuilder.toString();
		EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(epl);
		epStatement.addListener(CommonUpdateListener::update);

		Withdrawal withdrawal = new Withdrawal("doctor", BigDecimal.valueOf(123.50D));
		TimeUnit.SECONDS.sleep(2);
		epServiceProvider.getEPRuntime().sendEvent(withdrawal);
		TimeUnit.SECONDS.sleep(3);
	}

	/**
	 * 4.5. Output When Context Partition Ends
	 * 
	 * 每个事件都属于各自所属的Context（id等属性可以看出）。
	 * 我们可以自定义context，按照事件的某一个属性分类，
	 * epl表达式事件的窗口函数就在特定的context下起作用。
	 * 可以从下面的例子输出得出上面的结论。
	 */
	@Test
	public void test_Output_When_Context_Partition_Ends() throws Throwable {
		String context = "create context personBySexContext  partition by age from Person ";
		String epl = "context personBySexContext select *, context.name as contextName, context.id as contextId  from Person.win:length(1) ";
		epServiceProvider.getEPAdministrator().createEPL(context);
		epServiceProvider.getEPAdministrator().createEPL(epl).setSubscriber(new PersonSubscriber());
		TimeUnit.SECONDS.sleep(2);
		Person person = new Person("doctor who", "doctor", "man", 2000);
		epServiceProvider.getEPRuntime().sendEvent(person);

		TimeUnit.SECONDS.sleep(3);
		person = new Person("doctor who 2", "doctor", "man", 2000);
		epServiceProvider.getEPRuntime().sendEvent(person);

		TimeUnit.SECONDS.sleep(2);
		person = new Person("doctor who 3", "doctor", "man", 3100);
		epServiceProvider.getEPRuntime().sendEvent(person);
		TimeUnit.SECONDS.sleep(2);
		person = new Person("doctor who 5", "doctor", "man", 5100);
		epServiceProvider.getEPRuntime().sendEvent(person);

		// {new:{"age":2000,"firstName":"doctor","id":"85486e45-b45b-4442-8ad1-484b7fe8e40d","name":"doctor who","sex":"man"},contextName:personBySexContext,contextId:0}
		// {new:{"age":2000,"firstName":"doctor","id":"900588d8-b84c-454d-820b-a5285a4ea0d7","name":"doctor who 2","sex":"man"},contextName:personBySexContext,contextId:0}
		// {old:{"age":2000,"firstName":"doctor","id":"85486e45-b45b-4442-8ad1-484b7fe8e40d","name":"doctor who","sex":"man"},contextName:personBySexContext,contextId:0}
		// {new:{"age":3100,"firstName":"doctor","id":"828fff0c-8576-4f66-96c4-8e9e9fd1b14e","name":"doctor who 3","sex":"man"},contextName:personBySexContext,contextId:1}
		// {new:{"age":5100,"firstName":"doctor","id":"1fc60c94-6677-4439-a262-d1a024f1c5b6","name":"doctor who 5","sex":"man"},contextName:personBySexContext,contextId:2}
	}

	/**
	 * 4.2. Context Declaration
	 */
	@Test
	public void test_4_2_Context_Declaration() {
		String context = "create context personBySexContext partition by sex from Person  ";
		String window = "context personBySexContext  create window PersonWindow.win:length(3) as Person ";
		String epl = "insert into PersonWindow  select *, context.name, context.id from Person.win:length(3) ";
		String query = " select *, context.name , context.id from PersonWindow ";
		epServiceProvider.getEPAdministrator().createEPL(context);
		epServiceProvider.getEPAdministrator().createEPL(query).setSubscriber(new PersonSubscriber());
		Person person = new Person("doctor who", "doctor", "man", 2000);
		epServiceProvider.getEPRuntime().sendEvent(person);

	}

	public static class PersonSubscriber {
		public void update(Person person, String contextName, long contextId) {
			log.info("{new:{},contextName:{},contextId:{}}", person, contextName, contextId);
		}

		public void updateRStream(Person person, String contextName, long contextId) {
			log.info("{old:{},contextName:{},contextId:{}}", person, contextName, contextId);

		}
	}
}
