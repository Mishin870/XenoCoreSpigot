package com.mishin870.core.utils.containers;

import java.util.HashMap;
import java.util.Map;

public class StandardLanguageContainer implements ILanguageContainer {
	private static final int FORMATS_COUNT = 50;
	private static final String[] FORMATS = new String[FORMATS_COUNT];
	static {
		for (int i = 0; i < FORMATS_COUNT; i++) {
			FORMATS[i] = "{" + i + "}";
		}
	}
	
	private final Map<String, String> strings = new HashMap<>();
	
	public StandardLanguageContainer(Iterable<String> lines) {
		for (String line : lines) {
			if (line.trim().isEmpty()) {
				continue;
			}
			
			final String[] parts = line.split("=", 2);
			
			if (parts.length == 2) {
				strings.put(parts[0], parts[1]);
			}
		}
	}
	
	@Override
	public String get(String key, String... args) {
		if (!strings.containsKey(key)) {
			return key;
		}
		
		var result = strings.get(key);
		for (int i = 0; i < args.length; i++) {
			result = result.replace(FORMATS[i], args[i]);
		}
		
		return result;
	}
}