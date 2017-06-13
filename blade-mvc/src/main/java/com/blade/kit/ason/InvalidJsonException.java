package com.blade.kit.ason;

/**
 * @author Aidan Follestad (afollestad)
 */
public class InvalidJsonException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	InvalidJsonException(String json, Exception inner) {
		super("Invalid JSON: " + json, inner);
	}
}
