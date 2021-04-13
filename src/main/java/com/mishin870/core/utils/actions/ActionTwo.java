package com.mishin870.core.utils.actions;

@FunctionalInterface
public interface ActionTwo<A, B> {
	void run(A a, B b);
}
