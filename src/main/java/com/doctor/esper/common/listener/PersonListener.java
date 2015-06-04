package com.doctor.esper.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class PersonListener implements UpdateListener {
	private static final Logger log = LoggerFactory.getLogger(PersonListener.class);

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null && newEvents[0] != null) {
			log.info("{newEvents:{}}", JSON.toJSONString(newEvents));
		}
		if (oldEvents != null && oldEvents[0] != null) {
			log.info("{oldEvents:{}}", JSON.toJSONString(oldEvents));
		}
	}

}
