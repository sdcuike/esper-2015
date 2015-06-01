package com.doctor.esper.common;

import java.util.ArrayList;
import java.util.List;

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
	static List<EventBean> get(EPStatement epStatement) {
		List<EventBean> list = new ArrayList<>();
		SafeIterator<EventBean> safeIterator = epStatement.safeIterator();
		try {
			while (safeIterator.hasNext()) {
				list.add(safeIterator.next());
			}
		} catch (Throwable e) {
			safeIterator.close();
			e.printStackTrace();
		}
		return list;
	}
}
