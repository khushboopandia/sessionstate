package edu.khush.lsi.session.state;

import java.util.Date;

public class SessionData {

	private String sessionId;
	private int versionNo;
	private Date expirationTime;
	private String hostName;
	private String message="Hello User";

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	
	@Override
	public String toString() {
		return sessionId + "_" + String.valueOf(versionNo) + "_" + hostName;
	}

	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj == null || obj.getClass() == this.getClass())
			return false;

		SessionData o = (SessionData) obj;
		return o.sessionId.equals(this.sessionId);

	}

	@Override
	public int hashCode() {
		return sessionId.hashCode();

	}

}
