package com.my.bo;

import java.util.ArrayList;
import java.util.List;

public class Dto implements Bo {
	private String package_;
	private String className;
	private String serialNum;
	private boolean hasDate;
	private boolean hasBigDecimal;
	private String module;
	private String comment;

	private List<Field> fields = new ArrayList<Field>();

	public Dto(String module) {
		this.module = module;
	}

	public boolean isHasDate() {
		return hasDate;
	}

	public String getComment() {
		if (comment != null && comment.length() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("/**\n * ");
			sb.append(comment);
			sb.append("\n");
			sb.append(" */");
			return sb.toString();
		}
		return "";
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setHasDate(boolean hasDate) {
		this.hasDate = hasDate;
	}

	public void addField(Field e) {
		fields.add(e);
	}

	public List<Field> getFields() {
		return this.fields;
	}

	public String getPackage_() {
		return package_.replace("{layer}", getLayer()).replace("{module}", this.module);
	}

	public void setPackage_(String package_) {
		this.package_ = package_;
	}

	public String getClassName() {
		if (className.contains("_")) {
			String[] ary = className.split("_");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < ary.length; i++) {
				sb.append(ary[i].substring(0, 1).toUpperCase());
				sb.append(ary[i].substring(1));
			}
			return sb.toString();
		}

		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSerialNum() {
		String nanoTime = String.valueOf(System.nanoTime());
		serialNum = System.currentTimeMillis() + "" + nanoTime.substring(0, 5);
		return serialNum;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public static void main(String[] args) {
		String nanoTime = String.valueOf(System.nanoTime());
		System.out.println(nanoTime);
		System.out.println(System.currentTimeMillis() + "" + nanoTime.substring(9));
	}

	@Override
	public String getLayer() {
		return "model";
	}

	@Override
	public String getModule() {
		return this.module;
	}

	public boolean isHasBigDecimal() {
		return hasBigDecimal;
	}

	public void setHasBigDecimal(boolean hasBigDecimal) {
		this.hasBigDecimal = hasBigDecimal;
	}
}
