package com.doctor.esper.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.espertech.esper.client.EventBean;

public final class CommonUpdateListener {
	private static final Logger log = LoggerFactory.getLogger(CommonUpdateListener.class);

	public static void update(EventBean[] newEvents, EventBean[] oldEvents) {

		if (newEvents != null && newEvents[0] != null) {
			log.info("{newEvents:{}}", JSON.toJSONString(newEvents));
		}
		if (oldEvents != null && oldEvents[0] != null) {
			log.info("{oldEvents:{}}", JSON.toJSONString(oldEvents));
		}
	}
}
