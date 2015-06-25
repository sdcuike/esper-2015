package com.doctor.esper.reference_5_2_0;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.doctor.esper.event.HttpLog;
import com.doctor.esper.spring.EsperStatement;
import com.doctor.esper.spring.EsperTemplateBean;
import com.espertech.esper.event.map.MapEventBean;

/**
 * Chapter 18. Integration and Extension
 * 
 * @author doctor
 *
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#extension
 *
 * @time 2015年6月12日 下午3:19:55
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/esperConfigForChapter18/spring-esper.xml")
public class Chapter18IntegrationAndExtension {
	@Resource(name = "esperTemplateBean")
	private EsperTemplateBean esperTemplateBean;

	@Resource(name = "httpLogStatement")
	private EsperStatement httpLogStatement;

	/**
	 * 18.5. Aggregation Function-Implementing an Aggregation
	 */
	@Test
	public void test_Aggregation_Function() {
		HttpLog httpLog = new HttpLog();
		httpLog.setId(4);
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog();
		httpLog.setId(6);
		esperTemplateBean.sendEvent(httpLog);

		httpLog = new HttpLog();
		httpLog.setId(6);
		esperTemplateBean.sendEvent(httpLog);

		httpLog = new HttpLog();
		httpLog.setId(8);
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog();
		httpLog.setId(0);
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog();
		httpLog.setId(0);
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog();
		httpLog.setId(0);
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog();
		httpLog.setId(10);
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog();
		httpLog.setId(10);
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog();
		httpLog.setId(10);
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog();
		httpLog.setId(10);
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog();
		httpLog.setId(10);
		esperTemplateBean.sendEvent(httpLog);

		httpLog = new HttpLog();
		httpLog.setId(100);
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog();
		httpLog.setId(100);
		esperTemplateBean.sendEvent(httpLog);

		for (int i = 0; i < 10; i++) {
			httpLog = new HttpLog();
			httpLog.setId(100 + i);
			esperTemplateBean.sendEvent(httpLog);
		}

		List<Map<HttpLog, Long>> list = httpLogStatement.concurrentSafeQuery(eventBean -> {
			MapEventBean mapEventBean = (MapEventBean) eventBean;

			Map<HttpLog, Long> map = (Map<HttpLog, Long>) mapEventBean.get("topk");

			return map;
		});

		Map<HttpLog, Long> httopLogTopK = list.get(0);
		System.out.println(httopLogTopK);
	}
}
