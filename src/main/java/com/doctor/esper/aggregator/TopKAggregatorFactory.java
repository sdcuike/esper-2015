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

import java.util.Map;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

/**
 * @author doctor
 * 
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#epl-function-aggregation
 *      http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#custom-aggregation-function
 *      https://github.com/pulsarIO/jetstream-esper/blob/master/jetstream-esper/src/main/java/com/ebay/jetstream/event/processor/esper/aggregates/TopKAggregatorFactory.java
 *
 * @time 2015年6月25日 下午3:39:18
 */
public class TopKAggregatorFactory implements AggregationFunctionFactory {

	@Override
	public void setFunctionName(String functionName) {

	}

	@Override
	public void validate(AggregationValidationContext validationContext) {
		if (validationContext.getParameterTypes().length != 3) {
			throw new RuntimeException("TopKAggregator 自定义函数需要3个参数。");
		}

	}

	@Override
	public AggregationMethod newAggregator() {
		return new TopKAggregator();
	}

	@Override
	public Class getValueType() {
		return Map.class;
	}

}
