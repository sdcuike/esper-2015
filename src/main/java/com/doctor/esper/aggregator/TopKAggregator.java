/*
 * Copyright (C) 2014-present  The  esper-2015  Authors
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
package com.doctor.esper.aggregator;

import java.util.LinkedHashMap;
import java.util.Map;

import com.clearspring.analytics.stream.StreamSummary;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;

/**
 * @author doctor
 * 
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#epl-function-aggregation
 *      http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#custom-aggregation-function
 *      https://github.com/pulsarIO/jetstream-esper/blob/master/jetstream-esper/src/main/java/com/ebay/jetstream/event/processor/esper/aggregates/TopKAggregator.java
 * 
 *      Esper aggregation method for computing the topN elements of the elements provided to this aggregator
 *
 *      Usage: select topN(<maxCapacity>, <topNValue>, element) as uniqueElement from stream
 *
 * @time 2015年6月25日 下午3:07:06
 */
public class TopKAggregator implements AggregationMethod {
	StreamSummary<Object> counter;
	Integer capacity;
	Integer topN = Integer.valueOf(10);

	@Override
	public void enter(Object value) {
		Object[] values = (Object[]) value;

		if (counter == null) {
			capacity = (Integer) values[0];
			topN = (Integer) values[1];
			counter = new StreamSummary<Object>(capacity.intValue());
		}

		counter.offer(values[2]);
	}

	@Override
	public void leave(Object value) {

	}

	@Override
	public Object getValue() {
		LinkedHashMap<Object, Long> topK = new LinkedHashMap<>();

		if (counter == null) {
			return topK;
		}

		counter.topK(topN).forEach(e -> topK.put(e.getItem(), e.getCount()));

		return topK;
	}

	@Override
	public Class getValueType() {
		return Map.class;
	}

	@Override
	public void clear() {
		counter = null;
	}

}
