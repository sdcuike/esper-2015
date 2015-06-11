package com.doctor.esper.reference_5_2_0;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.doctor.esper.event.Person;
import com.doctor.esper.spring.EsperStatement;
import com.doctor.esper.spring.EsperTemplateBean;
import com.espertech.esper.client.EventBean;

/**
 * Chapter 10. EPL Reference: Functions
 * 
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#functionreference
 *      http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#epl-function-user-defined
 * 
 * @author doctor
 *
 * @time 2015年6月10日 下午3:22:32
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/esperConfigForChapter10/spring-esper.xml")
public class Chapter10EPLReferenceFunctions {

	@Resource(name = "esperTemplateBean")
	private EsperTemplateBean esperTemplateBean;

	@Resource(name = "personStatement")
	private EsperStatement personStatement;

	@Resource(name = "personStatementForFunction")
	private EsperStatement personStatementForFunction;

	@Resource(name = "personStatementForFunction2")
	private EsperStatement personStatementForFunction2;

	@Resource(name = "personStatementForFunction3")
	private EsperStatement personStatementForFunction3;

	/**
	 * 10.1. Single-row Function Reference
	 * 18.3. Single-Row Function
	 * 
	 * esper不仅支持自带的函数，还支持java类库静态方法及实例方法。
	 * 同时支持注册自定义实现函数名称。
	 * esper自动导入java一下包：
	 * java.lang.*
	 * java.math.*
	 * java.text.*
	 * java.util.*
	 * 
	 * esper自动导入包的xml配置如下：
	 * 
	 * <auto-import import-name="com.mycompany.mypackage.*"/>
	 * <auto-import import-name="com.mycompany.myapp.MyUtilityClass"/>
	 * 
	 * 也可以 用plugin-singlerow-function 注册返回单对象函数。
	 * 
	 */
	@Test
	public void test_single_row_function() {
		Person person = new Person("a", "simth", "man", 9);
		esperTemplateBean.sendEvent(person);
		person = new Person("b", "simth", "woman", 19);
		esperTemplateBean.sendEvent(person);
		person = new Person("c", "simth 博士", "woman", 29);
		esperTemplateBean.sendEvent(person);
		List<Person> list = personStatement.concurrentSafeQuery(Chapter10EPLReferenceFunctions::mapRow);
		System.out.println(list);// 用java函数使得每个人的年龄增加1

		// 自定义函数
		List<Person> list2 = personStatementForFunction.concurrentSafeQuery(Chapter10EPLReferenceFunctions::mapRow);
		System.out.println(list2);// 输出数据与上面一样

		List<Person> list3 = personStatementForFunction2.concurrentSafeQuery(Chapter10EPLReferenceFunctions::mapRow);
		System.out.println(list3);

		List<Person> list4 = personStatementForFunction3.concurrentSafeQuery(Chapter10EPLReferenceFunctions::mapRow);
		System.out.println(list4);
	}

	public static Person mapRow(EventBean eventBean) {
		Map<String, Object> map = (Map<String, Object>) eventBean.getUnderlying();
		String jsonString = JSON.toJSONString(map);
		return JSON.parseObject(jsonString, Person.class);
	}
}
