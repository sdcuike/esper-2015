package com.doctor.esper.spring;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.doctor.esper.event.Person;
import com.espertech.esper.client.EPServiceProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/spring-esper.xml")
public class EsperTemplateTest {

	@Resource(name = "esperTemplateBean")
	private EsperTemplateBean esperTemplateBean;

	@Resource(name = "esperTemplateBean2")
	private EsperTemplateBean esperTemplateBean2;

	@Resource(name = "personStatement")
	private EsperStatement personStatement;

	@Resource(name = "personQuery")
	private EsperQueryStatement personQuery;

	@Test
	public void test_EsperTemplateBean() throws ReflectiveOperationException {
		assertNotNull(esperTemplateBean);
		assertNotNull(esperTemplateBean.getEsperNativeRuntime());
		assertThat("spring-esperTemplateBean", equalTo(esperTemplateBean.getProviderURI()));
		assertTrue(esperTemplateBean.getStatements().isEmpty());

		Field field = esperTemplateBean.getClass().getSuperclass().getDeclaredField("epServiceProvider");
		field.setAccessible(true);
		EPServiceProvider provider = (EPServiceProvider) field.get(esperTemplateBean);
		provider.getEPAdministrator().createEPL("select * from Person");
	}

	@Test
	public void test_EsperTemplateBean2() throws ReflectiveOperationException {
		assertNotNull(esperTemplateBean2);
		assertNotNull(personStatement);
		List<Person> persons = Arrays.asList(new Person("William Hartnell", "", "man", 600),
				new Person("Patrick Troughton", "", "man", 800),
				new Person("Jon Pertwee", "", "man", 1000));

		persons.forEach(p -> esperTemplateBean2.sendEvent(p));

		List<Person> list = personStatement.concurrentSafeQuery(eventBean -> (Person) eventBean.getUnderlying());
		assertThat(2, equalTo(list.size()));
		assertTrue(Arrays.asList(new Person("Patrick Troughton", "", "man", 800), new Person("Jon Pertwee", "", "man", 1000)).equals(list));
	}

	@Test
	public void test_EsperQueryStatement() {
		List<Person> persons = Arrays.asList(new Person("William Hartnell", "", "man", 600),
				new Person("Patrick Troughton", "", "man", 800),
				new Person("Jon Pertwee", "", "man", 1000));

		persons.forEach(p -> esperTemplateBean2.sendEvent(p));
		assertNotNull(personQuery);
		List<Person> list = personQuery.executeQuery(eventBean -> (Person) eventBean.getUnderlying());
		System.out.println(list);
	}
}
