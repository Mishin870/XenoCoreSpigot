package com.mishin870.core.utils;

import java.util.Arrays;

public class CommandUtils {
	public static String merge(String[] args, int fromIndex) {
		return merge(args, fromIndex, " ");
	}
	
	public static String merge(String[] args, int fromIndex, String separator) {
		return String.join(separator, Arrays.asList(args).subList(fromIndex, args.length));
	}
}
