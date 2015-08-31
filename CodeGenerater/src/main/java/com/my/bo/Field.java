package com.my.bo;

public class Field {
	private String type;
	private String name;
	private String comment;
	private boolean primary;
	private String defaultValue;
	private String nullable;
	
	public String getColumnName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		if (name.contains("_")) {
			String[] ary = name.split("_");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < ary.length; i++) {
				if (i == 0) {
					sb.append(ary[i]);
				} else {
					sb.append(ary[i].substring(0, 1).toUpperCase());
					sb.append(ary[i].substring(1));
				}
			}
			return sb.toString();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public String getComment() {
		if (comment != null && comment.length() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("/**\n\t * ");
			sb.append(comment);
			sb.append("\n");
			sb.append("\t */");
			return sb.toString();
		}
		return "";
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSetter() {
		StringBuffer sb = new StringBuffer();
		sb.append("public void set");
		sb.append(this.getName().substring(0, 1).toUpperCase());
		sb.append(this.getName().substring(1));
		sb.append("(");
		sb.append(this.getType());
		sb.append(" ");
		sb.append(this.getName());
		sb.append("){\n");
		sb.append("\t\tthis.");
		sb.append(this.getName());
		sb.append("=");
		sb.append(this.getName());
		sb.append(";");
		sb.append("\n\t}");

		return sb.toString();
	}

	public String getGetter() {
		StringBuffer sb = new StringBuffer();
		sb.append("public ");
		sb.append(this.getType());
		sb.append(" ");
		sb.append("get");
		sb.append(this.getName().substring(0, 1).toUpperCase());
		sb.append(this.getName().substring(1));
		sb.append("(){\n");
		sb.append("\t\treturn this.");
		sb.append(this.getName());
		sb.append(";");
		sb.append("\n\t}");
		return sb.toString();
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
