package com.doctor.esper.reference;

import com.doctor.esper.common.EsperUtil;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;

/**
 * 3.5. Chapter 3. Processing Model -> Time Windows
 * 
 * @author doctor
 *
 * @time 2015年6月1日 下午4:58:33
 */
public class Chapter3ProcessingTimeWindows {
	private static final String config = "esper2015Config/esper-2015.esper.cfg.xml";

	public static void main(String[] args) {
		EPServiceProvider epServiceProvider = EsperUtil.esperConfig(config);
		String epl = "select account, avg(amount) from Withdrawal.win:time(4 sec) group by account,amount having amount > 1000";
		EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(epl);
	}

}
