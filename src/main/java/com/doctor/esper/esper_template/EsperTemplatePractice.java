package com.doctor.esper.esper_template;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.opencredo.esper.EsperStatement;
import org.opencredo.esper.EsperTemplate;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.doctor.esper.event.Person;

/**
 * @author doctor
 *
 * @time 2015年6月4日 下午3:13:36
 */
public class EsperTemplatePractice {

	public static void main(String[] args) throws InterruptedException {
		AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/esper2015Config/spring-esper.xml");
		EsperTemplate esperTemplate = applicationContext.getBean("esperTemplate", EsperTemplate.class);
		EsperStatement esperStatement = applicationContext.getBean("personStatement", EsperStatement.class);
		Person person = new Person("doctor who", "doctor", "man", 2000);
		esperTemplate.sendEvent(person);

		List<Person> list = esperStatement.concurrentSafeQuery(eventBean -> {
			return (Person) eventBean.getUnderlying();
		});

		System.out.println(list);

		TimeUnit.SECONDS.sleep(3);
		person = new Person("doctor who 2", "doctor", "man", 2000);
		esperTemplate.sendEvent(person);

		list = esperStatement.concurrentSafeQuery(eventBean -> {
			return (Person) eventBean.getUnderlying();
		});

		System.out.println(list);

		TimeUnit.SECONDS.sleep(2);
		person = new Person("doctor who 3", "doctor", "man", 3100);
		esperTemplate.sendEvent(person);

		list = esperStatement.concurrentSafeQuery(eventBean -> {
			return (Person) eventBean.getUnderlying();
		});

		System.out.println(list);

		TimeUnit.SECONDS.sleep(2);
		person = new Person("doctor who 5", "doctor", "man", 5100);
		esperTemplate.sendEvent(person);

		list = esperStatement.concurrentSafeQuery(eventBean -> {
			return (Person) eventBean.getUnderlying();
		});

		System.out.println(list);
	}

}
