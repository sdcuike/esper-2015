/*
 * Copyright (C) 2014- now() The  esper-2015  Authors
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
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.doctor.esper.event.HttpLog;
import com.doctor.esper.event.MyEvent;
import com.doctor.esper.spring.EsperQueryStatement;
import com.doctor.esper.spring.EsperTemplateBean;
import com.espertech.esper.client.EventBean;
import com.google.common.base.Stopwatch;

/**
 * Chapter 21. Performance
 * 
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#performance
 * 
 *      1.带有时间或长度数据窗口（time-based or length-based data windows）的EPL，根据它们的大小或长度也可能消耗大量的内存。对于时间窗口（ time-based data windows），你必须意识到内存的消耗取决于实际事件流的流入率（the actual event stream input throughput）。
 * 
 *      2.事件模式实例（pattern instances）同样消耗内存，特别是带有"every"关键字的模式（pattern）重复出现在子表达式中（ sub-expressions）。which again will depend on the actual event stream input throughput
 * 
 *      3.The processing of output events that your listener or subscriber performs temporarily blocks the thread until the processing completes, and may thus reduce throughput. It can therefore be beneficial for your application to process output events asynchronously and not block the Esper engine
 *      while an output event is being processed by your listener, especially if your listener code performs blocking IO operations.
 *      事件输出相关的监听器或订阅者，事件处理部分最好异步，或者阻塞操作放到其它线程中执行。
 * 
 *      4.Additionally, when reading input events from a store or network in a performance test, you may find that Esper processes events faster then you are able to feed events into Esper. In such case you may want to consider an in-memory driver for use in performance testing. Also consider
 *      decoupling your read operation from the event processing operation (sendEvent method) by having multiple readers or by pre-fetching your data from the store.
 *      当生成事件的操作处理速度跟不上esper处理事件的速度时候，可以考虑将生产事件的操作与处理事件即sendEvent 方法解藕，多线程生产事件或预生产事件。
 * 
 *      5.Select the underlying event rather than individual fields
 *      By selecting the underlying event in the select-clause we can reduce load on the engine, since the engine does not need to generate a new output event for each input event.
 *      Better performance select * from RFIDEvent,Less good performance select assetId, zone, xlocation, ylocation from RFIDEvent
 * 
 *      6.Prefer stream-level filtering over where-clause filtering.
 *      Esper stream-level filtering is very well optimized, while filtering via the where-clause post any data windows is not optimized.
 * 
 *      The same is true for named windows. If your application is only interested in a subset of named window data and such filters are not correlated to arriving events, place the filters into parenthesis after the named window name.
 *      Better performance : stream-level filtering select * from MarketData(ticker = 'GOOG'),
 *      Less good performance : post-data-window filtering select * from MarketData(ticker = 'GOOG')
 *      这条规则只适用于没有数据窗口的情况下（事件/长度窗口).当使用事件/长度窗口，语义变化了就。
 * 
 * @author doctor
 *
 * @time 2015年6月23日 下午1:42:03
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/esperConfigForChapter21/spring-esper.xml")
public class Chapter21Performance {

	@Resource(name = "esperTemplateBean")
	private EsperTemplateBean esperTemplateBean;

	@Resource(name = "httpLogWinLength100Query")
	private EsperQueryStatement httpLogWinLength100Query;

	/**
	 * We recommend using multiple threads to send events into Esper. We provide a test class below. Our test class does not use a blocking queue and thread pool so as to avoid a point of contention.
	 * 
	 * 推荐多线程向Esper引擎发送事件。
	 * 
	 * 
	 * We recommend using Java threads as test_testing_performance_with_multiple_threads() , or a blocking queue and thread pool with sendEvent() or alternatively we recommend configuring inbound threading if your application does not already employ threading. Esper provides the configuration
	 * option to use engine-level queues and threadpools for inbound, outbound and internal executions. See Section 15.7.1, “Advanced Threading” for more information.
	 * 
	 * 具体线程配置：http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#config-engine-threading
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_testing_performance_with_multiple_threads() throws InterruptedException {
		int numEvents = 1000000;
		int numThreads = 3;
		Thread[] threads = new Thread[numThreads];
		CountDownLatch countDownLatch = new CountDownLatch(numThreads);

		int eventPerThreads = numEvents / numThreads;
		for (int i = 0; i < numThreads; i++) {
			threads[i] = new Thread(new MyRunnable(countDownLatch, eventPerThreads, esperTemplateBean));
		}

		Stopwatch stopwatch = Stopwatch.createStarted();
		for (int i = 0; i < numThreads; i++) {
			threads[i].start();
		}
		countDownLatch.await(10, TimeUnit.MINUTES);
		if (countDownLatch.getCount() > 0) {
			throw new RuntimeException("Failed to complete in 10 minute");
		}

		System.out.println("Took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "milliseconds ");
	}

	private static class MyRunnable implements Runnable {
		private final CountDownLatch latch;
		private final int numEvents;
		private final EsperTemplateBean esperTemplateBean;

		public MyRunnable(CountDownLatch latch, int numEvents, EsperTemplateBean esperTemplateBean) {
			this.latch = latch;
			this.numEvents = numEvents;
			this.esperTemplateBean = esperTemplateBean;
		}

		@Override
		public void run() {
			Random random = new Random();
			for (int i = 0; i < numEvents; i++) {
				esperTemplateBean.sendEvent(new MyEvent(random.nextInt(512)));
			}

			latch.countDown();
		}

	}

	/**
	 * 21.2.35. Query Planning Index Hints
	 * 
	 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#perf-tips-25b
	 * 
	 *      验证查询计划执行索引功能： @Hint('index(httpLogWinLength100ForIndex, bust)')，利用bust。Multiple indexes can be listed separated by comma (,).
	 * 
	 */
	@Test
	public void test_Query_Planning() {
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

		Stopwatch stopwatch = Stopwatch.createStarted();
		List<HttpLog> list = httpLogWinLength100Query.prepareQueryWithParameters(Chapter21Performance::httpLogMapRow, "www.baidu.com/tieba_son", 2, 10);
		System.out.println(stopwatch.elapsed(TimeUnit.MICROSECONDS));
		stopwatch.stop();
		assertThat(list.size(), equalTo(2));
		System.out.println(list);
	}

	private static HttpLog httpLogMapRow(EventBean eventBean) {
		return (HttpLog) eventBean.getUnderlying();
	}
}
