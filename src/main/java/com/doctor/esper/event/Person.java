package com.doctor.esper.event;

import java.util.Objects;
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
		this.name = name;
		this.firstName = firstName;
		this.sex = sex;
		this.age = age;
	}

	public Person(UUID id, String name, String firstName, String sex, int age) {
		this.id = id;
		this.name = name;
		this.firstName = firstName;
		this.sex = sex;
		this.age = age;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setAge(int age) {
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
	public int hashCode() {
		return Objects.hash(getId(), getName(), getFirstName(), getSex(), getAge());
	}

	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
