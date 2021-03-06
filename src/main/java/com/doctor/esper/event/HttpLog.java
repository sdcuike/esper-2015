package com.doctor.esper.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;

import com.alibaba.fastjson.JSON;

public class HttpLog implements Serializable {

	private static final long serialVersionUID = -4462410669092068376L;

	private int id;

	private String machineId;

	private String requestPath;

	private String referer;

	// private String ip;

	private String userAgent;

	private LocalDateTime time;

	public HttpLog() {

	}

	public HttpLog(int id, String machineId, String requestPath, String referer, String userAgent, LocalDateTime time) {
		this.id = id;
		this.machineId = machineId;
		this.requestPath = requestPath;
		this.referer = referer;
		this.userAgent = userAgent;
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMachineId() {
		return machineId;
	}

	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(Arrays.asList(this.id).toArray());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof HttpLog) {
			HttpLog log = (HttpLog) obj;

			return log.id == this.id;
		}
		return false;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
