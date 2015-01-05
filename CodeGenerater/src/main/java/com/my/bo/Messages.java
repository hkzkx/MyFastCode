package com.my.bo;


public class Messages implements Bo {

	private Dto dto;
	private String module;

	public Messages(String module) {
		this.module = module;
	}

	public String getPackage_() {
		return module.concat(".").concat(dto.getClassName()).toLowerCase();
	}

	@Override
	public String getClassName() {
		return getDto().getClassName().toLowerCase();
	}

	public String getiClassName() {
		return "I" + getClassName();
	}

	public String getInstanceName() {
		String name = getClassName();
		StringBuffer sb = new StringBuffer();
		sb.append(name.substring(0, 1).toLowerCase());
		sb.append(name.substring(1));
		return sb.toString();
	}

	@Override
	public String getLayer() {
		return "service";
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
