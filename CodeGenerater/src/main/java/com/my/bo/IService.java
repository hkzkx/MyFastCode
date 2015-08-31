package com.my.bo;

public class IService implements Bo {

	private String package_;
	private Dto dto;
	private String module;

	public IService(String module) {
		this.module = module;
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
	public String getClassName() {
		return "I" + dto.getClassName() + "Service";
	}

	@Override
	public String getLayer() {
		return "service";
	}

	@Override
	public String getModule() {
		return this.module;
	}

}
