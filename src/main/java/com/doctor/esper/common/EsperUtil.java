package com.doctor.esper.common;

import java.util.ArrayList;
import java.util.List;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.SafeIterator;

/**
 * @author docotr
 *
 * @time 2015年6月1日 下午4:28:33
 */
public enum EsperUtil {
	;
	public static List<EventBean> get(EPStatement epStatement) {
		List<EventBean> list = new ArrayList<>();
		SafeIterator<EventBean> safeIterator = epStatement.safeIterator();
		try {
			while (safeIterator.hasNext()) {
				list.add(safeIterator.next());
			}
		} catch (Throwable e) {
			safeIterator.close();
			e.printStackTrace();
		} finally {
			safeIterator.close();
		}
		return list;
	}

	/**
	 * @see 15.2. The Service Provider Interface
	 * 
	 *      EPServiceProvider就代表一个esper引擎实例。 一个esper引擎的实例与其它引擎的实例是没有关联性的（独立性）。
	 *      每个引擎的实例都有自己的administrative and runtime interface.
	 *      一个引擎的实例有许多个，获得实例的EPServiceProviderManager方法为getDefaultProvider和getProvider(String providerURI)。
	 *      后者可以用于根据不同的获得providerURI，引擎可以生成不同实例。EPServiceProviderManager根据providerURI存在与否，
	 *      获取已经存在的实例或者新的实例。
	 * 
	 * 
	 * @param config
	 * @return
	 */
	public static EPServiceProvider esperConfig(String config) {
		Configuration configuration = new Configuration();
		configuration.configure(EsperUtil.class.getClassLoader().getResource(config));
		return EPServiceProviderManager.getDefaultProvider(configuration);
	}
}
