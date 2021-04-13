package com.mishin870.core.utils.containers;

public class EmptyLanguageContainer implements ILanguageContainer {
	@Override
	public String get(String key, String... args) {
		return key;
	}
}