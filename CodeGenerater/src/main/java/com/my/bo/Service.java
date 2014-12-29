package com.my.bo;

public class Service implements Bo {

	private String package_;
	private Dao dao;
	private String module;

	public Service(String module) {
		this.module = module;
	}

	public String getPackage_() {
		return package_.replace("{layer}", getLayer()).replace("{module}", this.module);
	}

	public void setPackage_(String package_) {
		this.package_ = package_;
	}

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

	@Override
	public String getClassName() {
		return dao.getDto().getClassName() + "Service";
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
}
