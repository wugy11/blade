package com.blade.jdbc.core;

import com.blade.jdbc.model.SqlOpts;

/**
 * 组装sql时的列信息
 */
public class AutoField {

	/** 名称 */
	private String name;

	/** 操作符 and or */
	private String sqlOperator = "";

	/** 本身操作符 值大于、小于、in等 */
	private String fieldOperator = "";

	/** 值 */
	private Object[] values;

	private SqlOpts type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public SqlOpts getType() {
		return type;
	}

	public void setType(SqlOpts type) {
		this.type = type;
	}

	public String getSqlOperator() {
		return sqlOperator;
	}

	public void setSqlOperator(String sqlOperator) {
		this.sqlOperator = sqlOperator;
	}

	public String getFieldOperator() {
		return fieldOperator;
	}

	public void setFieldOperator(String fieldOperator) {
		this.fieldOperator = fieldOperator;
	}

}
