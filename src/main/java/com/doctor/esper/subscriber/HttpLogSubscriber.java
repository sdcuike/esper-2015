package com.doctor.esper.subscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doctor.esper.event.HttpLog;

/**
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#api-admin-subscriber
 * 
 *      The engine can deliver results to your subscriber in two ways:
 * 
 *      1.Each evert in the insert stream results in a method invocation, and each event in the remove stream results in further method invocations. This is termed row-by-row delivery.
 * 
 *      2. A single method invocation that delivers all rows of the insert and remove stream. This is termed multi-row delivery.
 * 
 * 
 * @author doctor
 *
 * @time 2015年6月11日 下午3:51:08
 */
public class HttpLogSubscriber {
	private static final Logger log = LoggerFactory.getLogger(HttpLogSubscriber.class);

	/**
	 * Row-By-Row Delivery
	 * 
	 * @param httpLog
	 * @param count
	 */
	public void update(HttpLog httpLog, long count) {
		log.warn("报警阈值httplog{}超过访问次数{}", httpLog, count);
	}
}
