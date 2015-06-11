package com.doctor.esper.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @see http://www.espertech.com/esper/release-5.2.0/esper-reference/html_single/index.html#custom-singlerow-function
 * 
 *      18.3.1. Implementing a Single-Row Function
 * 
 * @author doctor
 *
 * @time 2015年6月10日 下午4:06:46
 */
public final class MyUtilityClass {

	/**
	 * @see java.lang.Math.incrementExact(int)
	 * @param a
	 * @return
	 */
	public static int incrementExact(int a) {
		if (a == Integer.MAX_VALUE) {
			throw new ArithmeticException("integer overflow");
		}
		return a + 1;
	}

	/**
	 * 中文分词统计
	 * 
	 * @param input
	 * @param content
	 * @return
	 */
	public static int wordCount(String input, String content) {
		if (StringUtils.isBlank(input) || StringUtils.isBlank(content)) {
			return 0;
		}
		if (input.length() < 2 || content.length() < 2) {
			return 0;
		}

		int sum = 0;
		input = StringUtils.deleteWhitespace(getChinese(input));
		content = StringUtils.deleteWhitespace(getChinese(content));

		for (int i = 0, i_length = input.length(); i < i_length - 1; i++) {
			String word = input.substring(i, i + 2);
			for (int j = 0, j_length = content.length(), index = content.indexOf(word, j); j < j_length - 1 && index != -1; j = index + 1, index = content.indexOf(word, j)) {

				sum++;
			}

		}
		return sum;

	}

	public static String getChinese(final String content) {
		return content.replaceAll("[a-zA-Z]", " ").replaceAll("\\s+", " ");
	}

	public static void main(String[] args) {

		System.out.println(wordCount("中果中s,", "中果中s,sa果中果"));

	}
}
