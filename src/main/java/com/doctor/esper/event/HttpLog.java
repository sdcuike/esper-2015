package com.doctor.esper.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HttpLog implements Serializable {

	private static final long serialVersionUID = -4462410669092068376L;

	private Long id;

	private String machineId;

	private String requestPath;

	private String referer;

	// private String ip;

	private String userAgent;

	private LocalDateTime time;

	public HttpLog(Long id, String machineId, String requestPath, String referer, String userAgent, LocalDateTime time) {
		this.id = id;
		this.machineId = machineId;
		this.requestPath = requestPath;
		this.referer = referer;
		this.userAgent = userAgent;
		this.time = time;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

}
