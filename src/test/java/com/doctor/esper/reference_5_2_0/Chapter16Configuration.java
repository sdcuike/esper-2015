package com.doctor.esper.reference_5_2_0;

import java.sql.Timestamp;
import java.util.List;

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
	 * 
	 * 16.4.9.8. SQL Types Mapping <br/>
	 * java类型与sql类型 {@linkplain java.sql.Types java.sql.Types} 之间的映射，esper提供自由配置。<br/>
	 * 
	 * 配置格式：
	 * <sql-types-mapping sql-type="2" java-type="int" />
	 * 
	 * 其中sql-type 指的是{@linkplain java.sql.Types java.sql.Types.NUMERIC}值。
	 * java-type 指的是java类型(大小写不区分）。
	 * 
	 * 
	 * Any window, such as the time window, generates insert stream (istream) events as events enter the window, and remove stream (rstream) events as events leave the window. The engine executes the given SQL query for each CustomerCallEvent in both the insert stream and the remove stream. As a
	 * performance optimization, the istream or rstream keywords in the select clause can be used to instruct the engine to only join insert stream or remove stream events, reducing the number of SQL query executions.
	 * 
	 * 可以认为窗口数据与sql数据源jion，查询出的数据又放回数据窗口内。
	 */
	@Test
	public void test_Relational_Database_Access() {
		HttpLog httpLog = new HttpLog();
		httpLog.setId(4);
		esperTemplateBean.sendEvent(httpLog); // 只发送一个事件，查询结果为1个（结果来源于符合条件的数据库的数据）
		httpLog = new HttpLog();
		httpLog.setId(6);
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog();
		httpLog.setId(8);
		esperTemplateBean.sendEvent(httpLog); // 事件流窗口定义为2,结果有两个数据输出（数据库存在值前提）。
		httpLog = new HttpLog();
		httpLog.setId(0);
		esperTemplateBean.sendEvent(httpLog);
		List<HttpLog> list = nginxlogStatement.concurrentSafeQuery(eventBean -> {
			MapEventBean mapEventBean = (MapEventBean) eventBean;

			HttpLog log = new HttpLog();
			log.setId((int) mapEventBean.get("id"));
			log.setMachineId((String) mapEventBean.get("machineId"));
			log.setReferer((String) mapEventBean.get("referer"));
			log.setRequestPath((String) mapEventBean.get("requestPath"));
			log.setUserAgent((String) mapEventBean.get("userAgent"));
			Timestamp time = (Timestamp) mapEventBean.get("time");
			log.setTime(time.toLocalDateTime());
			return log;
		});
		System.out.println(list);
	}
}
