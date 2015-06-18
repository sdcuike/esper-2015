/*
 * Copyright (C) 2014- now() The  ${project_name}  Authors
 *
 * https://github.com/sdcuike
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doctor.esper.reference_5_2_0;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.doctor.esper.event.HttpLog;
import com.doctor.esper.spring.EsperQueryStatement;
import com.doctor.esper.spring.EsperTemplateBean;
import com.espertech.esper.client.EPOnDemandPreparedQueryParameterized;
import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EventBean;

/**
 * Chapter 6. EPL Reference: Named Windows And Tables
 * 
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#nwtable-overview
 *      http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#epl-syntax-time-periods
 *      http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#epl-from-clause-view-spec
 *      http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#view-parameters
 * 
 * 
 *      As a general rule-of-thumb, if you need to share a data window between statements, the named window is the right approach.
 *      If however rows are organized by primary key or hold aggregation state, a table may be preferable.
 *      EPL statements allow the combined use of both.
 * 
 *      One important difference between named windows and tables is in the data that a row holds: While named windows hold events,
 *      tables can hold additional derived state.
 * 
 * @author doctor
 *
 * @time 2015年6月11日 上午11:44:05
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/esperConfigForChapter6/spring-esper.xml")
public class Chapter6EPLReferenceNamedWindowsAndTables {
	@Resource(name = "esperTemplateBean")
	private EsperTemplateBean esperTemplateBean;

	@Resource(name = "httpLogWinTime10SecQuery")
	private EsperQueryStatement httpLogWinTime10SecQuery;

	@Resource(name = "httpLogWinLength100Query")
	private EsperQueryStatement httpLogWinLength100Query;

	/**
	 * 6.2.2. Inserting Into Named Windows
	 * 时间和长度视图都是FIFO缓存形式。
	 * jdbc方式查询
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_Inserting_Into_Named_Windows() throws InterruptedException {
		HttpLog httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);

		httpLog = new HttpLog(2, UUID.randomUUID().toString(), "www.baidu.com/tie", "www.baidu.com/ba", "userAgent 2", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);

		List<HttpLog> list = httpLogWinTime10SecQuery.executeQuery(Chapter6EPLReferenceNamedWindowsAndTables::httpLogMapRow);
		System.out.println(list);

		TimeUnit.SECONDS.sleep(5);
		List<HttpLog> list2 = httpLogWinTime10SecQuery.executeQuery(Chapter6EPLReferenceNamedWindowsAndTables::httpLogMapRow);
		System.out.println(list2);

		httpLog = new HttpLog(3, UUID.randomUUID().toString(), "www.baidu.com/tie", "www.baidu.com/ba", "userAgent 2", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(4, UUID.randomUUID().toString(), "www.baidu.com/tie", "www.baidu.com/ba", "userAgent 2", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(5, UUID.randomUUID().toString(), "www.baidu.com/tie", "www.baidu.com/ba", "userAgent 2", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);

		list = httpLogWinTime10SecQuery.executeQuery(Chapter6EPLReferenceNamedWindowsAndTables::httpLogMapRow);
		System.out.println(list);

		TimeUnit.SECONDS.sleep(5);
		for (int i = 0; i < 10; i++) {
			httpLog = new HttpLog(i + 15, UUID.randomUUID().toString(), "www.baidu.com/tie", "www.baidu.com/ba", "userAgent 2", LocalDateTime.now());
			esperTemplateBean.sendEvent(httpLog);

			list = httpLogWinTime10SecQuery.executeQuery(Chapter6EPLReferenceNamedWindowsAndTables::httpLogMapRow);
			System.out.println(list);
			TimeUnit.SECONDS.sleep(2);
		}

	}

	/**
	 * jdbc方式查询
	 * 
	 * @throws InterruptedException
	 * 
	 */
	@Test
	public void test_jdbc_like_query() throws InterruptedException {
		HttpLog httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);

		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(11, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);

		String sql = "select * from HttpLogWindowTime5Sec where id = ?";
		EPOnDemandPreparedQueryParameterized queryWithParameters = esperTemplateBean.getEsperNativeRuntime().prepareQueryWithParameters(sql);
		queryWithParameters.setObject(1, 1);
		EPOnDemandQueryResult result = esperTemplateBean.getEsperNativeRuntime().executeQuery(queryWithParameters);
		EventBean[] beans = result.getArray();

		assertThat(beans.length, equalTo(5));
		System.out.println("jdbc查询数目:" + beans.length);
		Stream.of(beans).map(eventBean -> (HttpLog) eventBean.getUnderlying()).forEach(System.out::println);

		TimeUnit.SECONDS.sleep(10);
		result = esperTemplateBean.getEsperNativeRuntime().executeQuery(queryWithParameters);
		beans = result.getArray();

		assertThat(beans.length, equalTo(0));

	}

	/**
	 * Chapter 3. Processing Model
	 * 3.7. Aggregation and Grouping
	 * 订阅者实现监听
	 * 
	 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#processingmodel_aggregation
	 *      http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#config-variables
	 *      http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#variable_using
	 *      http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#config-engine-variables
	 * 
	 * @param eventBean
	 * @return
	 * @throws InterruptedException
	 */
	@Test
	public void test_() throws InterruptedException {
		HttpLog httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		TimeUnit.SECONDS.sleep(10);

		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
	}

	/**
	 * 6.9. Explicitly Indexing Named Windows and Tables
	 * 
	 * 为窗口或表创建索引
	 * 
	 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#named_explicit_index
	 * 
	 * @see com.espertech.esper.epl.fafquery.FireAndForgetQueryExec
	 * 
	 * 
	 * 
	 *      EPRuntimeImpl.executeQuery(EPOnDemandPreparedQueryParameterized) line: 1541
	 *      EPRuntimeImpl.executeQueryInternal(String, EPStatementObjectModel, EPOnDemandPreparedQueryParameterized, ContextPartitionSelector[]) line: 1553
	 * 
	 *      EPPreparedExecuteMethodQuery.execute(ContextPartitionSelector[]) line: 198
	 * 
	 *      EPPreparedExecuteMethodQuery.getStreamFilterSnapshot(int, ContextPartitionSelector) line: 258
	 *      FireAndForgetProcessorNamedWindow.getProcessorInstance(AgentInstanceContext) line: 45
	 *      FireAndForgetInstanceNamedWindow.<init>(NamedWindowProcessorInstance) line: 29
	 *      EPPreparedExecuteMethodQuery.getStreamSnapshotInstance(int, List<ExprNode>, FireAndForgetInstance) line: 294
	 *      NamedWindowTailViewInstance.snapshot(FilterSpecCompiled, Annotation[]) line: 209
	 *      NamedWindowTailViewInstance.snapshotNoLock(FilterSpecCompiled, Annotation[]) line: 270
	 *      FireAndForgetQueryExec.snapshot(FilterSpecCompiled, Annotation[], VirtualDWView, EventTableIndexRepository, boolean, Log, String, AgentInstanceContext) line: 53
	 *      (index索引处理部分)
	 *      NamedWindowTailViewInstance.snapshotNoLock(FilterSpecCompiled, Annotation[]) line: 279
	 *      （通过索引result，为null，就返回所有数据，然后在过滤）
	 * @param eventBean
	 * @return
	 */

	@Test
	public void test_Explicitly_Indexing_Named_Window() {
		HttpLog httpLog = new HttpLog(1, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(2, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(3, UUID.randomUUID().toString(), "www.baidu.com/tieba_son", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(4, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(6, UUID.randomUUID().toString(), "www.baidu.com/tieba", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);
		httpLog = new HttpLog(8, UUID.randomUUID().toString(), "www.baidu.com/tieba_son", "www.baidu.com", "userAgent", LocalDateTime.now());
		esperTemplateBean.sendEvent(httpLog);

		List<HttpLog> list = httpLogWinLength100Query.prepareQueryWithParameters(Chapter6EPLReferenceNamedWindowsAndTables::httpLogMapRow, "www.baidu.com/tieba_son", 2, 10);

		System.out.println(list);
	}

	private static HttpLog httpLogMapRow(EventBean eventBean) {
		return (HttpLog) eventBean.getUnderlying();
	}
}
