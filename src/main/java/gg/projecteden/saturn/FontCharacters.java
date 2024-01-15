package gg.projecteden.saturn;

import com.google.gson.Gson;
import gg.projecteden.saturn.FontCharacters.FontFile.Provider;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FontCharacters {
	final Path FONT_FILE_PATH = Paths.get("assets/minecraft/font/default.json");
	private static final String FOLDER = "src/main/resources/characters/";
	private static final List<String> TYPES = List.of("chinese", "unicode");

	@Test
	@SneakyThrows
	void chars() {
		final FontFile fontFile = new Gson().fromJson(Files.readString(FONT_FILE_PATH), FontFile.class);

		for (String type : TYPES) {
			int removed = 0;
			final List<String> chars = new ArrayList<>(Arrays.asList(Files.readString(Path.of(FOLDER + type + ".txt")).split("")));

			for (Provider provider : fontFile.providers)
				if (provider.chars != null)
					for (String characterLine : provider.chars) {
						for (String character : Arrays.asList(characterLine.split(""))) {
							if (chars.contains(character)) {
								++removed;
								chars.remove(character);
							}
						}
					}

			System.out.println("Removed " + removed + " characters");
			System.out.println("Available " + type + " characters: " + chars);
		}
	}

	@Data
	static class FontFile {
		private List<Provider> providers = new ArrayList<>();

		@Data
		static class Provider {
			private String file;
			private List<String> chars;
		}
	}
}
