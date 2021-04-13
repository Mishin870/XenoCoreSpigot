package com.mishin870.core.utils.containers;

public class MirrorLanguageContainer implements ILanguageContainer {
	private final ILanguageContainer origin;
	
	public MirrorLanguageContainer(ILanguageContainer origin) {
		this.origin = origin;
	}
	
	@Override
	public String get(String key, String... args) {
		return origin.get(key, args);
	}
}
