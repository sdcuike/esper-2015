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
package com.doctor.esper.subscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doctor.esper.event.MyEvent;

/**
 * @author doctor
 *
 * @time 2015年6月23日 下午2:34:19
 */
public class MyEventSubscriber {
	private static final Logger log = LoggerFactory.getLogger(MyEventSubscriber.class);

	public void update(MyEvent myEvent, long count) {
		log.info("{}:{},count:{}", this.getClass().getName(), myEvent, count);
	}
}
