package com.doctor.esper.reference_5_2_0;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.doctor.esper.spring.EsperStatement;
import com.doctor.esper.spring.EsperTemplateBean;

/**
 * @author doctor
 *
 * @time 2015年6月9日 下午4:07:55
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/esperConfigForChapter16/spring-esper-database.xml")
public class Chapter16Configuration {
	@Resource(name = "esperTemplateBean")
	private EsperTemplateBean esperTemplateBean;

	@Resource(name = "nginxlogStatement")
	private EsperStatement nginxlogStatement;

	/**
	 * 16.4.9. Relational Database Access
	 * 5.13. Accessing Relational Data via SQL
	 */
	@Test
	public void test_Relational_Database_Access() {

	}
}
