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

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.doctor.esper.event.MyEvent;
import com.doctor.esper.spring.EsperTemplateBean;
import com.google.common.base.Stopwatch;

/**
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#performance
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
 * @author doctor
 *
 * @time 2015年6月23日 下午1:42:03
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/esperConfigForChapter21/spring-esper.xml")
public class Chapter21Performance {

	@Resource(name = "esperTemplateBean")
	private EsperTemplateBean esperTemplateBean;

	/**
	 * We recommend using multiple threads to send events into Esper. We provide a test class below. Our test class does not use a blocking queue and thread pool so as to avoid a point of contention.
	 * 
	 * 推荐多线程向Esper引擎发送事件。下面的测试，没用阻塞队列和线程池技术避免性能损失。
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
}
