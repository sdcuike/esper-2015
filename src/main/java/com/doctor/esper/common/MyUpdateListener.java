package com.doctor.esper.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;

public final class MyUpdateListener {
	private static final Logger log = LoggerFactory.getLogger(MyUpdateListener.class);

	public static void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null && newEvents[0] != null) {
			log.info("{newEvents:{}}", newEvents[0]);
		}
		if (oldEvents != null && oldEvents[0] != null) {
			log.info("{oldEvents:{}}", oldEvents[0]);
		}
	}
}
