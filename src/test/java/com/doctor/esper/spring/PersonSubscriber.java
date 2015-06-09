package com.doctor.esper.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doctor.esper.event.Person;

public class PersonSubscriber {
	private static final Logger log = LoggerFactory.getLogger(PersonSubscriber.class);

	public void update(Person person) {
		log.info("{new:{}}", person);
	}

	public void updateRStream(Person person) {
		log.info("{old:{}", person);

	}
}
