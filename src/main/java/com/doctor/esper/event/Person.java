package com.doctor.esper.event;

import java.util.UUID;

import com.alibaba.fastjson.JSON;

/**
 * @author doctor
 *
 * @time 2015年6月3日 下午4:37:08
 */
public class Person {
	private UUID id;
	private String name;
	private String firstName;
	private String sex;
	private int age;

	public Person(String name, String firstName, String sex, int age) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.firstName = firstName;
		this.sex = sex;
		this.age = age;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getSex() {
		return sex;
	}

	public int getAge() {
		return age;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
