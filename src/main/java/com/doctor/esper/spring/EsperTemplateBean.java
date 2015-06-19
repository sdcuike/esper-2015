package com.doctor.esper.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @see org.opencredo.esper.spring.EsperTemplateBean
 * 
 * @author doctor
 *
 * @time 2015年6月8日 下午5:16:51
 */
public class EsperTemplateBean extends EsperTemplate implements InitializingBean, DisposableBean {
	private final static Logger LOG = LoggerFactory.getLogger(EsperTemplateBean.class);

	public void afterPropertiesSet() {
		LOG.debug("Initializing the esper template bean");
		super.initialize();
		LOG.debug("Completed initializing the esper template bean");
	}

	public void destroy() {
		LOG.debug("Destroying the esper template bean");
		super.cleanup();
		LOG.debug("Finished destroying the esper template bean");
	}
}
