package com.my.bo;

public class Dao implements Bo {
	private String package_;
	private Dto dto;
	private String module;

	public Dao(String module) {
		this.module = module;
	}

	public String getInstanceName() {
		String name = getClassName();
		StringBuffer sb = new StringBuffer();
		sb.append(name.substring(0, 1).toLowerCase());
		sb.append(name.substring(1));
		return sb.toString();
	}

	public String getClassName() {
		return dto.getClassName() + "Dao";
	}

	public String getPackage_() {
		return package_.replace("{layer}", getLayer()).replace("{module}", this.module);
	}

	public void setPackage_(String package_) {
		this.package_ = package_;
	}

	public Dto getDto() {
		return dto;
	}

	public void setDto(Dto dto) {
		this.dto = dto;
	}

	@Override
	public String getLayer() {
		return "dao";
	}

	@Override
	public String getModule() {
		return this.module;
	}

}
