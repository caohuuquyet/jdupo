package com.jdupo.data;

import java.io.Serializable;

public class Device implements Serializable {   

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String description;
	private Object location;
	private int inputPower;
	private String inputPowerUnit;
	private String currentDeviceStatus;
	private String statusStartTime;
	private Object dataCloud;
	
	public Device() {
		
    }
	
	public Device(String id,String description,  Object location, int inputPower,
			String inputPowerUnit, String currentDeviceStatus,
			String statusStartTime, Object dataCloud) {
		super();
		this.id = id;
		this.description = description;			
		this.location = location;
		this.inputPower = inputPower;
		this.inputPowerUnit = inputPowerUnit;
		this.currentDeviceStatus = currentDeviceStatus;
		this.statusStartTime = statusStartTime;
		this.dataCloud = dataCloud;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setLocation(Object location) {
		this.location = location;
	}

	public void setInputPower(int inputPower) {
		this.inputPower = inputPower;
	}

	public void setInputPowerUnit(String inputPowerUnit) {
		this.inputPowerUnit = inputPowerUnit;
	}

	public void setCurrentDeviceStatus(String currentDeviceStatus) {
		this.currentDeviceStatus = currentDeviceStatus;
	}

	public void setStatusStartTime(String statusStartTime) {
		this.statusStartTime = statusStartTime;
	}

	public void setDataCloud(Object dataCloud) {
		this.dataCloud = dataCloud;
	}

	public String getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}

	public Object getLocation() {
		return location;
	}

	public int getInputPower() {
		return inputPower;
	}

	public String getInputPowerUnit() {
		return inputPowerUnit;
	}

	public String getCurrentDeviceStatus() {
		return currentDeviceStatus;
	}

	public String getStatusStartTime() {
		return statusStartTime;
	}

	public Object getDataCloud() {
		return dataCloud;
	}

	@Override
	public int hashCode() {
		int _hash = 0;
		_hash += (getId() != null ? getId().hashCode() : 0);
		return _hash;
	}

	@Override
	public boolean equals(Object _object) {
		if (!(_object instanceof Device)) {
			return false;
		}
		Device _other = (Device) _object;
		if ((this.getId() == null && _other.getId() != null)
				|| (this.getId() != null && !this.getId()
						.equals(_other.getId()))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Device[id=" + getId() + "]" + serialVersionUID;
	}

	

}
