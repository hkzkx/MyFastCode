package com.my.bo;

public class Controller implements Bo {
	private String package_;
	private Service service;
	private String module;

	public Controller(String module) {
		this.module = module;
	}

	public String getPackage_() {
		return package_.replace("{layer}", getLayer()).replace("{module}", this.module);
	}

	public void setPackage_(String package_) {
		this.package_ = package_;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	@Override
	public String getClassName() {
		return service.getDao().getDto().getClassName() + "Controller";
	}

	@Override
	public String getLayer() {
		return "controller";
	}

	@Override
	public String getModule() {
		return this.module;
	}

}
