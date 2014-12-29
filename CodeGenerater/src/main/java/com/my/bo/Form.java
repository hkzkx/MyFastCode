package com.my.bo;

public class Form implements Bo {
	private Dto dto;
	private String module;

	public Form(String module) {
		this.module = module;
	}

	public String getPackage_() {
		return module.concat(".").concat(dto.getClassName()).toLowerCase();
	}

	@Override
	public String getClassName() {
		return "form";
	}

	@Override
	public String getLayer() {
		return "ftl";
	}

	@Override
	public String getModule() {
		return this.module;
	}

	public Dto getDto() {
		return dto;
	}

	public void setDto(Dto dto) {
		this.dto = dto;
	}

}
