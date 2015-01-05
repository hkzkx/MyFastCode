package my.db;

public enum DBKey {
	SRC("/src.properties"), DST("/dst.properties");

	String key;

	DBKey(String key) {
		this.key = key;
	}

	public String value() {
		return key;
	}
}
