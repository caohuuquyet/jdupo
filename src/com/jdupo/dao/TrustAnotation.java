package com.jdupo.dao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TrustAnotation {

	final static Charset ENCODING = StandardCharsets.UTF_8;
	final static String FILE_NAME = "./samples/pl0.dfl";
	final static String OUTPUT_FILE_NAME = "./samples/pl1.dfl";

	public static void main(String[] args) throws IOException {

		ReadWriteTextFileJDK7 text = new ReadWriteTextFileJDK7();

		List<String> lines = Arrays.asList("Down to the Waterline",
				"Water of Love");
		text.writeLargerTextFile(OUTPUT_FILE_NAME, lines);

	}

	void writeLargerTextFile(String aFileName, List<String> aLines)
			throws IOException {
		Path path = Paths.get(aFileName);
		try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)) {
			for (String line : aLines) {
				writer.write(line);
				writer.newLine();
			}
		}
	}

}
