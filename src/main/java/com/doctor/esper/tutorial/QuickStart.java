package com.doctor.esper.tutorial;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

/**
 * code for
 * 
 * @see http://www.espertech.com/esper/quickstart.php
 * 
 * @author doctor
 *
 * @time 2015年5月28日 下午3:51:18
 */
public class QuickStart {

	public static void main(String[] args) {

		// Configuration
		//
		// Esper runs out of the box and no configuration is required. However configuration can help make statements more readable and provides the
		// opportunity to plug-in extensions and to configure relational database access.
		//
		// One useful configuration item specifies Java package names from which to take event classes.
		//
		// This snippet of using the configuration API makes the Java package of the OrderEvent class known to an engine instance:
		// In order to query the OrderEvent events, we can now remove the package name from the statement:see line35

		Configuration configuration = new Configuration();
		configuration.addEventTypeAutoName("com.doctor.esper.tutorial");
		// Creating a Statement
		// A statement is a continuous query registered with an Esper engine instance that provides results to listeners as new data arrives, in
		// real-time, or by demand via the iterator (pull) API.
		// The next code snippet obtains an engine instance and registers a continuous query. The query returns the average price over all OrderEvent
		// events that arrived in the last 30 seconds:
		EPServiceProvider epServiceProvider = EPServiceProviderManager.getDefaultProvider(configuration);
		String expression = "select avg(price) from OrderEvent.win:time(30 sec)";
		EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(expression);

		// By attaching the listener to the statement the engine provides the statement's results to the listener:
		MyListener myListener = new MyListener();
		epStatement.addListener(myListener);

		// Sending events
		// The runtime API accepts events for processing. As a statement's results change, the engine indicates the new results to listeners right
		// when the events are processed by the engine.
		// Sending events is straightforward as well:
		OrderEvent orderEvent = new OrderEvent("shirt", 75.50D);
		epServiceProvider.getEPRuntime().sendEvent(orderEvent);

	}

}
