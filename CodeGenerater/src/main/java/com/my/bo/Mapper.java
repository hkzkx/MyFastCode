package com.my.bo;

public class Mapper implements Bo {
	private String package_;
	private Dao dao;
	private String module;

	public void setPackage_(String package_) {
		this.package_ = package_;
	}

	public Mapper(String module) {
		this.module = module;
	}

	@Override
	public String getClassName() {
		return getDao().getDto().getClassName() + "Mapper";
	}

	@Override
	public String getPackage_() {
		return package_.replace("{layer}", getLayer()).replace("{module}", this.module);
	}

	@Override
	public String getLayer() {
		return "mapper";
	}

	@Override
	public String getModule() {
		return this.module;
	}

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

}
