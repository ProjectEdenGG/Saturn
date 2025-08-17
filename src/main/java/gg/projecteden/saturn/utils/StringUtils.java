package gg.projecteden.saturn.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import lombok.SneakyThrows;

import java.io.StringWriter;

public class StringUtils extends gg.projecteden.api.common.utils.StringUtils {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@SneakyThrows
	public static String toStringWithTabs(JsonObject json) {
		StringWriter stringWriter = new StringWriter();
		try (var jsonWriter = new JsonWriter(stringWriter)) {
			jsonWriter.setIndent("\t");
			GSON.toJson(json, jsonWriter);
			return stringWriter.toString();
		}
	}

}
