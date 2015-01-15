package my.CodeGenerater;

public enum DataType {

	Long(new String[] { "bigint" }), 
	String(new String[] { "varchar", "text", "char" }), 
	Date("datetime", "timestamp"),
	Integer(new String[] { "int", "smallint", "tinyint" }), 
	Float("float"), 
	BigDecimal("decimal"), 
	Double("double");

	private String[] sqlType;

	DataType(String... sqlType) {
		this.sqlType = sqlType;
	}

	public static DataType getType(String sqlType) {
		for (DataType type : values()) {
			if (type.sqlType.length == 1 && type.sqlType[0].equals(sqlType)) {
				return type;
			}
			for (String sqlType_ : type.sqlType) {
				if (sqlType_.equals(sqlType)) {
					return type;
				}
			}
		}
		return null;
	}
}
