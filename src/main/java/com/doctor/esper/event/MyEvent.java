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
package com.doctor.esper.event;

import com.alibaba.fastjson.JSON;

/**
 * for 21.2.3. Theading
 * 
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#performance
 *
 * @author doctor
 *
 * @time 2015年6月23日 下午2:20:26
 */
public final class MyEvent {
	private final int id;

	public MyEvent(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
