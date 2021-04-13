package com.mishin870.core.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class StreamUtils {
	public static Stream<String> readLines(InputStream stream) {
		return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines();
	}
	
	public static String[] readLinesArray(InputStream stream) {
		return (String[]) readLines(stream).toArray();
	}
}
