package com.blade.kit.ason;

/**
 * @author Aidan Follestad (afollestad)
 */
public class InvalidPathException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	InvalidPathException(String message) {
		super(message);
	}
}
