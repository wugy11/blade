package com.blade.jdbc.core;

import java.util.List;

import com.blade.jdbc.exceptions.AssistantException;
import com.blade.jdbc.model.PageRow;
import com.blade.jdbc.model.SqlOpts;
import com.blade.kit.CollectionKit;

/**
 * 条件操作
 */
public class Take {

	/** 操作的实体类 */
	private Class<?> entityClass;

	/** 操作的字段 */
	private List<AutoField> autoFields;

	/** 排序字段 */
	private List<AutoField> orderByFields;

	/** where标识 */
	private boolean isWhere = false;

	private PageRow pageRow;

	public Take(Class<?> clazz) {
		this.entityClass = clazz;
		this.autoFields = CollectionKit.newArrayList();
		this.orderByFields = CollectionKit.newArrayList();
	}

	/**
	 * 初始化
	 */
	public static Take create(Class<?> clazz) {
		return new Take(clazz);
	}

	public Take orderby(String field) {
		AutoField autoField = this.buildAutoFields(field, null, null, SqlOpts.ORDER_BY, new Object[0]);
		this.orderByFields.add(autoField);

		return this;
	}

	/**
	 * asc 排序属性
	 */
	public Take asc(String... field) {
		for (String f : field) {
			AutoField autoField = this.buildAutoFields(f, null, SqlOpts.ASC, SqlOpts.ORDER_BY, new Object[0]);
			this.orderByFields.add(autoField);
		}
		return this;
	}

	/**
	 * desc 排序属性
	 */
	public Take desc(String... field) {
		for (String f : field) {
			AutoField autoField = this.buildAutoFields(f, null, SqlOpts.DESC, SqlOpts.ORDER_BY, new Object[0]);
			this.orderByFields.add(autoField);
		}
		return this;
	}

	/**
	 * 设置操作属性
	 */
	public Take set(String fieldName, Object value) {
		AutoField autoField = this.buildAutoFields(fieldName, null, SqlOpts.EQ, SqlOpts.UPDATE, value);
		this.autoFields.add(autoField);
		return this;
	}

	/**
	 * 设置主键值名称，如oracle序列名，非直接的值
	 */
	public Take setPKValueName(String pkName, String valueName) {
		AutoField autoField = this.buildAutoFields(pkName, null, SqlOpts.EQ, SqlOpts.PK_VALUE_NAME, valueName);
		this.autoFields.add(autoField);
		return this;
	}

	/**
	 * 设置and条件
	 */
	public Take and(String fieldName, Object... values) {
		this.and(fieldName, SqlOpts.EQ, values);
		return this;
	}

	/**
	 * 设置and条件
	 */
	public Take and(String fieldName, SqlOpts fieldOperator, Object... values) {
		AutoField autoField = this.buildAutoFields(fieldName, SqlOpts.AND, fieldOperator, SqlOpts.WHERE, values);
		this.autoFields.add(autoField);
		return this;
	}

	public Take like(String fieldName, String value) {
		return this.and(fieldName, SqlOpts.LIKE, String.format("%s%s%s", "%", value, "%"));
	}

	public Take between(String fieldName, Object a, Object b) {
		return this.and(fieldName, SqlOpts.BETWEEN, a, b);
	}

	public Take notBetween(String fieldName, Object a, Object b) {
		return this.and(fieldName, SqlOpts.NOT_BETWEEN, a, b);
	}

	public Take gt(String fieldName, Object value) {
		return this.and(fieldName, SqlOpts.GT, value);
	}

	public Take gtE(String fieldName, Object value) {
		return this.and(fieldName, SqlOpts.GE, value);
	}

	public Take lt(String fieldName, Object value) {
		return this.and(fieldName, SqlOpts.LT, value);
	}

	public Take ltE(String fieldName, Object value) {
		return this.and(fieldName, SqlOpts.LE, value);
	}

	public Take eq(String fieldName, Object value) {
		return this.and(fieldName, SqlOpts.EQ, value);
	}

	public Take notEq(String fieldName, Object value) {
		return this.and(fieldName, SqlOpts.NEQ, value);
	}

	public <T> Take in(String fieldName, List<T> values) {
		return this.and(fieldName, SqlOpts.IN, values.toArray());
	}

	public Take in(String fieldName, Object... values) {
		return this.and(fieldName, SqlOpts.IN, values);
	}

	public <T> Take notIn(String fieldName, List<T> values) {
		return this.notIn(fieldName, values.toArray());
	}

	public Take notIn(String fieldName, Object... values) {
		return this.and(fieldName, SqlOpts.NOT_IN, values);
	}

	/**
	 * 设置or条件
	 */
	public Take or(String fieldName, Object... values) {
		this.or(fieldName, SqlOpts.EQ, values);
		return this;
	}

	/**
	 * 设置or条件
	 */
	public Take or(String fieldName, SqlOpts fieldOperator, Object... values) {
		AutoField autoField = this.buildAutoFields(fieldName, SqlOpts.OR, fieldOperator, SqlOpts.WHERE, values);
		this.autoFields.add(autoField);
		return this;
	}

	/**
	 * 设置where条件属性
	 */
	public Take where(String fieldName, SqlOpts fieldOperator, Object... values) {
		if (this.isWhere) {
			throw new AssistantException("There can be only one 'where'!");
		}
		AutoField autoField = this.buildAutoFields(fieldName, SqlOpts.AND, fieldOperator, SqlOpts.WHERE, values);
		this.autoFields.add(autoField);
		this.isWhere = true;
		return this;
	}

	public Take page(int page, int limit, String orderby) {
		this.pageRow = new PageRow(page, limit, orderby);
		return this;
	}

	public Take page(int page, int limit) {
		return this.page(page, limit, null);
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public List<AutoField> getAutoFields() {
		return autoFields;
	}

	public List<AutoField> getOrderByFields() {
		return orderByFields;
	}

	public PageRow getPageRow() {
		return pageRow;
	}

	/**
	 * 获取操作的字段
	 */
	private AutoField buildAutoFields(String fieldName, SqlOpts sqlOperator, SqlOpts fieldOperator, SqlOpts type,
			Object... values) {
		AutoField autoField = new AutoField();
		autoField.setName(fieldName);
		if (null != sqlOperator)
			autoField.setSqlOperator(sqlOperator.getValue());
		if (null != fieldOperator)
			autoField.setFieldOperator(fieldOperator.getValue());
		autoField.setValues(values);
		autoField.setType(type);

		if (type == SqlOpts.WHERE) {
			this.isWhere = true;
		}

		return autoField;
	}

	public boolean hasWhere() {
		return isWhere;
	}
}
