package com.mishin870.core.utils.rotator;

public class SimpleRotator<T> implements Rotator<T> {
	private final T[] items;
	private int current = -1;
	
	@SafeVarargs
	public SimpleRotator(T... items) {
		this.items = items;
		
		if (items.length == 0) {
			throw new IllegalArgumentException("No elements provided to rotator");
		}
	}
	
	public void reset() {
		current = 0;
	}
	
	public T next() {
		current++;
		
		if (current >= items.length) {
			current = 0;
		}
		
		return items[current];
	}
}
