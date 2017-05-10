package com.blade.jdbc.model;

public enum SqlOpts {

	EQ("="), NEQ("<>"), 
	LT("<"), LE("<="), 
	GT(">"), GE(">="), 
	AND("and"), OR("or"), 
	LIKE("like"), IN("in"), 
	ASC("asc"), DESC("desc"), 
	NOT_IN("not in"), BETWEEN("between"), 
	NOT_BETWEEN("not between"), INSERT_INTO("insert into "),
	
	UPDATE("update"), WHERE("where"),
	ORDER_BY("order by"), PK_VALUE_NAME(""),
	;

	private String value;

	public String getValue() {
		return value;
	}

	private SqlOpts(String value) {
		this.value = value;
	}

}
