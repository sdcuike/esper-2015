package com.doctor.esper.spring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPStatementState;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.SafeIterator;
import com.espertech.esper.client.UpdateListener;

/**
 * @author doctor
 * 
 * @see org.opencredo.esper.EsperStatement
 *
 * @time 2015年6月8日 下午5:02:19
 */
public class EsperStatement implements EsperStatementOperation {
	private final static Logger LOG = LoggerFactory.getLogger(EsperStatement.class);

	private String epl;
	private EPStatement epStatement;
	private Set<UpdateListener> listeners = new LinkedHashSet<UpdateListener>();
	private Object subscriber;
	private String subscriberMethodName = null;

	public EsperStatement(String epl) {
		this.epl = epl;
	}

	public String getEPL() {
		return epl;
	}

	public EPStatementState getState() {
		return this.epStatement.getState();
	}

	/**
	 * Starts events being collated according to the statement's filter query
	 */
	public void start() {
		if (LOG.isInfoEnabled()) {
			LOG.info("Esper statement [" + epl + "] being started");
		}

		this.epStatement.start();

		if (LOG.isInfoEnabled()) {
			LOG.info("Esper statement [" + epl + "] started");
		}
	}

	/**
	 * Stops the underlying native statement from applying its filter query.
	 */
	public void stop() {
		if (LOG.isInfoEnabled()) {
			LOG.info("Esper statement [" + epl + "] being stopped");
		}

		this.epStatement.stop();

		if (LOG.isInfoEnabled()) {
			LOG.info("Esper statement [" + epl + "] stopped");
		}
	}

	/**
	 * Provides a mechanism by which to access the underlying esper API
	 * 
	 * @param callback
	 *            used to pass access to the underlying esper API resources
	 */
	public void doWithNativeEPStatement(NativeEPStatementCallback callback) {
		callback.executeWithEPStatement(this.epStatement, this.epl);
	}

	public void setListeners(Set<UpdateListener> listeners) {
		this.listeners = listeners;

		this.refreshEPStatmentListeners();
	}

	public Set<UpdateListener> getListeners() {
		return this.listeners;
	}

	public void setSubscriber(Object subscriber) {
		this.subscriber = subscriber;
	}

	public void setSubscriberMethodName(String subscriberMethodName) {
		this.subscriberMethodName = subscriberMethodName;
	}

/**
	 * Adds an {@link UpdateListener) to the statement to support
	 * the 'push' mode of retrieving results.
	 * 
	 * @param listener The listener to be invoked when appropriate results according to the EPL filter query.
	 */
	public void addListener(UpdateListener listener) {
		listeners.add(listener);

		this.refreshEPStatmentListeners();

		this.addEPStatementListener(listener);
	}

	/**
	 * Refreshes the listeners associated with this statement.
	 */
	private void refreshEPStatmentListeners() {
		for (UpdateListener listener : this.listeners) {
			this.addEPStatementListener(listener);
		}
	}

	/**
	 * Adds an listener to the underlying native EPStatement.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	private void addEPStatementListener(UpdateListener listener) {
		if (this.subscriber == null) {
			if (epStatement != null) {
				epStatement.addListener(listener);
			}
		}
	}

	/**
	 * Sets the native Esper statement. Typically created by an Esper Template.
	 * 
	 * @param epStatement
	 *            the underlying native Esper statement
	 * @see org.opencredo.esper.EsperTemplate
	 */
	void setEPStatement(EPStatement epStatement) {
		this.epStatement = epStatement;
		if (this.subscriber != null && this.subscriberMethodName == null) {
			epStatement.setSubscriber(this.subscriber);
		} else if (this.subscriber != null && this.subscriberMethodName != null) {
			epStatement.setSubscriber(this.subscriber, subscriberMethodName);
		} else {
			for (UpdateListener listener : listeners) {
				epStatement.addListener(listener);
			}
		}
	}

	public <T> List<T> concurrentSafeQuery(RowMapper<T> rm) {
		LOG.info("Concurrent safe query being executed");

		if (epStatement.isStopped() || epStatement.isDestroyed()) {
			LOG
					.error("Concurrent safe query was attempted when the statement was stopped or destroyed");
			throw new EsperStatementInvalidStateException(
					"Attempted to execute a concurrent safe query when esper statement resource had state of "
							+ epStatement.getState());
		}

		SafeIterator<EventBean> safeIter = this.epStatement.safeIterator();

		List<T> objectList = new ArrayList<T>();
		try {
			for (; safeIter.hasNext();) {
				EventBean event = safeIter.next();
				objectList.add(rm.mapRow(event));
			}
		} finally {
			safeIter.close();
		}

		LOG.info("Concurrent safe query was completed");
		return objectList;
	}

	public <T> T concurrentSafeQueryForObject(RowMapper<T> rm) {
		LOG.info("Concurrent safe query for object being executed");

		if (epStatement.isStopped() || epStatement.isDestroyed()) {
			LOG
					.error("Concurrent safe query for object was attempted when the statement was stopped or destroyed");
			throw new EsperStatementInvalidStateException(
					"Attempted to execute a concurrent safe query for object when esper statement resource had state of "
							+ epStatement.getState());
		}

		SafeIterator<EventBean> safeIter = this.epStatement.safeIterator();

		T result = null;
		try {
			// Only retrieve the last result
			while (safeIter.hasNext()) {
				EventBean event = safeIter.next();
				if (!safeIter.hasNext()) {
					result = rm.mapRow(event);
				}

			}
		} finally {
			safeIter.close();
		}

		LOG.info("Concurrent safe query for object was completed");
		return result;
	}

	public <T> List<T> concurrentUnsafeQuery(RowMapper<T> rm) {
		LOG.info("Concurrent unsafe query being executed");

		if (epStatement.isStopped() || epStatement.isDestroyed()) {
			LOG
					.error("Concurrent unsafe query was attempted when the statement was stopped or destroyed");
			throw new EsperStatementInvalidStateException(
					"Attempted to execute a concurrent unsafe query when esper statement resource had state of "
							+ epStatement.getState());
		}

		Iterator<EventBean> safeIter = this.epStatement.iterator();

		List<T> objectList = new ArrayList<T>();
		for (; safeIter.hasNext();) {
			EventBean event = safeIter.next();
			objectList.add(rm.mapRow(event));
		}

		LOG.info("Concurrent unsafe query was completed");
		return objectList;
	}

	public <T> T concurrentUnsafeQueryForObject(
			RowMapper<T> rm) {
		LOG.info("Concurrent unsafe query for object being executed");

		if (epStatement.isStopped() || epStatement.isDestroyed()) {
			LOG
					.error("Concurrent unsafe query for object was attempted when the statement was stopped or destroyed");
			throw new EsperStatementInvalidStateException(
					"Attempted to execute a concurrent unsafe query for object when esper statement resource had state of "
							+ epStatement.getState());
		}

		Iterator<EventBean> safeIter = this.epStatement.iterator();

		T result = null;

		// Only retrieve the last result
		while (safeIter.hasNext()) {
			EventBean event = safeIter.next();
			if (!safeIter.hasNext()) {
				result = rm.mapRow(event);
			}

		}

		LOG.info("Concurrent unsafe query for object was completed");
		return result;
	}
}
