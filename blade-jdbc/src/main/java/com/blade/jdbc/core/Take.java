package com.blade.jdbc.core;

import java.util.Arrays;
import java.util.List;

import com.blade.jdbc.exceptions.AssistantException;
import com.blade.jdbc.model.PageRow;
import com.blade.jdbc.model.QueryOpts;
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

	/** 白名单 */
	private List<String> includeFields;

	/** 黑名单 */
	private List<String> excludeFields;

	/** where标识 */
	private boolean isWhere = false;

	private PageRow pageRow;

	/**
	 * constructor
	 *
	 * @param clazz
	 */
	public Take(Class<?> clazz) {
		this.entityClass = clazz;
		this.autoFields = CollectionKit.newArrayList();
		this.orderByFields = CollectionKit.newArrayList();
	}

	/**
	 * 初始化
	 * 
	 * @param clazz
	 * @return
	 */
	public static Take create(Class<?> clazz) {
		return new Take(clazz);
	}

	/**
	 * 添加白名单
	 *
	 * @param field
	 * @return
	 */
	public Take include(String... field) {
		if (this.includeFields == null) {
			this.includeFields = CollectionKit.newArrayList();
		}
		this.includeFields.addAll(Arrays.asList(field));
		return this;
	}

	/**
	 * 添加黑名单
	 *
	 * @param field
	 * @return
	 */
	public Take exclude(String... field) {
		if (this.excludeFields == null) {
			this.excludeFields = CollectionKit.newArrayList();
		}
		this.excludeFields.addAll(Arrays.asList(field));
		return this;
	}

	public Take orderby(String field) {
		AutoField autoField = this.buildAutoFields(field, null, "", AutoField.ORDER_BY_FIELD, new Object[0]);
		this.orderByFields.add(autoField);

		return this;
	}

	/**
	 * asc 排序属性
	 *
	 * @param field
	 *            the field
	 * @return
	 */
	public Take asc(String... field) {
		for (String f : field) {
			AutoField autoField = this.buildAutoFields(f, null, QueryOpts.ASC, AutoField.ORDER_BY_FIELD, new Object[0]);
			this.orderByFields.add(autoField);
		}
		return this;
	}

	/**
	 * desc 排序属性
	 *
	 * @param field
	 *            the field
	 * @return
	 */
	public Take desc(String... field) {
		for (String f : field) {
			AutoField autoField = this.buildAutoFields(f, null, QueryOpts.DESC, AutoField.ORDER_BY_FIELD,
					new Object[0]);
			this.orderByFields.add(autoField);
		}
		return this;
	}

	/**
	 * 设置操作属性
	 *
	 * @param fieldName
	 *            the field name
	 * @param value
	 *            the value
	 * @return
	 */
	public Take set(String fieldName, Object value) {
		AutoField autoField = this.buildAutoFields(fieldName, null, QueryOpts.EQ, AutoField.UPDATE_FIELD, value);
		this.autoFields.add(autoField);
		return this;
	}

	/**
	 * 设置主键值名称，如oracle序列名，非直接的值
	 * 
	 * @param pkName
	 * @param valueName
	 * @return
	 */
	public Take setPKValueName(String pkName, String valueName) {
		AutoField autoField = this.buildAutoFields(pkName, null, QueryOpts.EQ, AutoField.PK_VALUE_NAME, valueName);
		this.autoFields.add(autoField);
		return this;
	}

	/**
	 * 设置and条件
	 *
	 * @param fieldName
	 * @param values
	 * @return
	 */
	public Take and(String fieldName, Object... values) {
		this.and(fieldName, QueryOpts.EQ, values);
		return this;
	}

	/**
	 * 设置and条件
	 *
	 * @param fieldName
	 * @param fieldOperator
	 * @param values
	 * @return
	 */
	public Take and(String fieldName, String fieldOperator, Object... values) {
		AutoField autoField = this.buildAutoFields(fieldName, QueryOpts.AND, fieldOperator, AutoField.WHERE_FIELD,
				values);
		this.autoFields.add(autoField);
		return this;
	}

	public Take like(String fieldName, String value) {
		return this.and(fieldName, QueryOpts.LIKE, String.format("%s%s%s", "%", value, "%"));
	}

	public Take between(String fieldName, Object a, Object b) {
		return this.and(fieldName, QueryOpts.BETWEEN, a, b);
	}

	public Take notBetween(String fieldName, Object a, Object b) {
		return this.and(fieldName, QueryOpts.NOT_BETWEEN, a, b);
	}

	public Take gt(String fieldName, Object value) {
		return this.and(fieldName, QueryOpts.GT, value);
	}

	public Take gtE(String fieldName, Object value) {
		return this.and(fieldName, QueryOpts.GE, value);
	}

	public Take lt(String fieldName, Object value) {
		return this.and(fieldName, QueryOpts.LT, value);
	}

	public Take ltE(String fieldName, Object value) {
		return this.and(fieldName, QueryOpts.LE, value);
	}

	public Take eq(String fieldName, Object value) {
		return this.and(fieldName, QueryOpts.EQ, value);
	}

	public Take notEq(String fieldName, Object value) {
		return this.and(fieldName, QueryOpts.NEQ, value);
	}

	public <T> Take in(String fieldName, List<T> values) {
		return this.and(fieldName, QueryOpts.IN, values.toArray());
	}

	public Take in(String fieldName, Object... values) {
		return this.and(fieldName, QueryOpts.IN, values);
	}

	public <T> Take notIn(String fieldName, List<T> values) {
		return this.notIn(fieldName, values.toArray());
	}

	public Take notIn(String fieldName, Object... values) {
		return this.and(fieldName, QueryOpts.NOT_IN, values);
	}

	/**
	 * 设置or条件
	 *
	 * @param fieldName
	 * @param values
	 * @return
	 */
	public Take or(String fieldName, Object... values) {
		this.or(fieldName, QueryOpts.EQ, values);
		return this;
	}

	/**
	 * 设置or条件
	 *
	 * @param fieldName
	 * @param fieldOperator
	 * @param values
	 * @return
	 */
	public Take or(String fieldName, String fieldOperator, Object... values) {
		AutoField autoField = this.buildAutoFields(fieldName, QueryOpts.OR, fieldOperator, AutoField.WHERE_FIELD,
				values);
		this.autoFields.add(autoField);
		return this;
	}

	/**
	 * 设置where条件属性
	 *
	 * @param fieldName
	 *            the field name
	 * @param fieldOperator
	 *            the operator
	 * @param values
	 *            the values
	 * @return
	 */
	public Take where(String fieldName, String fieldOperator, Object... values) {
		if (this.isWhere) {
			throw new AssistantException("There can be only one 'where'!");
		}
		AutoField autoField = this.buildAutoFields(fieldName, QueryOpts.AND, fieldOperator, AutoField.WHERE_FIELD,
				values);
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

	public List<String> getIncludeFields() {
		return includeFields;
	}

	public List<String> getExcludeFields() {
		return excludeFields;
	}

	public List<AutoField> getOrderByFields() {
		return orderByFields;
	}

	public PageRow getPageRow() {
		return pageRow;
	}

	/**
	 * 获取操作的字段
	 *
	 * @return
	 */
	private AutoField buildAutoFields(String fieldName, String sqlOperator, String fieldOperator, int type,
			Object... values) {
		AutoField autoField = new AutoField();
		autoField.setName(fieldName);
		autoField.setSqlOperator(sqlOperator);
		autoField.setFieldOperator(fieldOperator);
		autoField.setValues(values);
		autoField.setType(type);

		if (type == AutoField.WHERE_FIELD) {
			this.isWhere = true;
		}

		return autoField;
	}

	public boolean hasWhere() {
		return isWhere;
	}
}
