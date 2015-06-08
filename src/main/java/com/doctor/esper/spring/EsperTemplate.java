package com.doctor.esper.spring;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UnmatchedListener;

/**
 * @see org.opencredo.esper.EsperTemplate
 * 
 * @author doctor
 *
 * @time 2015年6月8日 下午5:10:06
 */
public class EsperTemplate implements EsperTemplateOperation {
	private final static Logger LOG = LoggerFactory.getLogger(EsperTemplate.class);

	private EPServiceProvider epServiceProvider;
	private EPRuntime epRuntime;
	private String name;
	private Set<EsperStatement> statements = new LinkedHashSet<>();
	private Resource configuration;
	private UnmatchedListener unmatchedListener;
	private volatile boolean initialised = false;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setStatements(Set<EsperStatement> statements) {
		this.statements = statements;
	}

	public void setConfiguration(Resource configuration) {
		this.configuration = configuration;
	}

	public void setUnmatchedListener(UnmatchedListener unmatchedListener) {
		this.unmatchedListener = unmatchedListener;
	}

	public EPRuntime getEsperNativeRuntime() {
		return epRuntime;
	}

	public Set<EsperStatement> getStatements() {
		return this.statements;
	}

	public synchronized void addStatement(EsperStatement statement) {
		statements.add(statement);
		if (this.initialised) {
			EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(statement.getEPL());
			statement.setEPStatement(epStatement);
		}
	}

	public void sendEvent(Object event) throws InvalidEsperConfigurationException {
		LOG.debug("Sending event to Esper");
		if (epRuntime != null) {
			epRuntime.sendEvent(event);
		} else {
			LOG.error("Attempted to send message with null Esper Runtime.");
			throw new InvalidEsperConfigurationException(
					"Esper Runtime is null. Have you initialized the template before you attempt to send an event?");
		}
		LOG.debug("Sent event to Esper");
	}

	public synchronized void initialize() throws InvalidEsperConfigurationException {
		if (this.initialised) {
			throw new InvalidEsperConfigurationException("EsperTemplate should only be initialised once");
		}
		this.initialised = true;
		LOG.debug("Initializing esper template");
		try {
			configureEPServiceProvider();
			epRuntime = epServiceProvider.getEPRuntime();
			if (this.unmatchedListener != null) {
				epRuntime.setUnmatchedListener(unmatchedListener);
			}
			setupEPStatements();
		} catch (Exception e) {
			LOG.error("An exception occured when attempting to initialize the esper template", e);
			throw new InvalidEsperConfigurationException(e.getMessage(), e);
		}
		LOG.debug("Finished initializing esper template");
	}

	public void cleanup() {
		epServiceProvider.destroy();
	}

	/**
	 * Add the appropriate statements to the esper runtime.
	 */
	private void setupEPStatements() {
		for (EsperStatement statement : statements) {
			EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(statement.getEPL());
			statement.setEPStatement(epStatement);
		}
	}

	/**
	 * Configure the Esper Service Provider to create the appropriate Esper
	 * Runtime.
	 * 
	 * @throws IOException
	 * @throws EPException
	 */
	private void configureEPServiceProvider() throws EPException, IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Configuring the Esper Service Provider with name: " + name);
		}
		if (this.configuration != null && this.configuration.exists()) {
			Configuration esperConfiguration = new Configuration();
			esperConfiguration = esperConfiguration.configure(this.configuration.getFile());
			epServiceProvider = EPServiceProviderManager.getProvider(name, esperConfiguration);
			LOG.info("Esper configured with a user-provided configuration", esperConfiguration);
		} else {
			epServiceProvider = EPServiceProviderManager.getProvider(name);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Completed configuring the Esper Service Provider with name: " + name);
		}
	}
}
