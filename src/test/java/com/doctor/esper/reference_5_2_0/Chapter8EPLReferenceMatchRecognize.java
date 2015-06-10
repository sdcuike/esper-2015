package com.doctor.esper.reference_5_2_0;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Chapter 8. EPL Reference: Match Recognize
 * 
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#match-recognize-intro
 * 
 *      Match Recognize 的定义与正则表达式相似。对字符串，可以用正则表达式寻找感兴趣的文本。Match Recognize只不过是去匹配感兴趣的
 *      事件流而已。
 *      Match Recognize 不仅用在事件流中到达的新事件，还可以用在用iterator pull-API查询数上（必须是a named window or data window on a stream，
 *      tables cannot be used in the from-clause with match-recognize)。
 * 
 * @author doctor
 *
 * @time 2015年6月10日 下午5:25:14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/esperConfigForChapter8/spring-esper.xml")
public class Chapter8EPLReferenceMatchRecognize {

}
