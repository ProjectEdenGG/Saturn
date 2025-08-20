package gg.projecteden.saturn.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static gg.projecteden.saturn.utils.StringUtils.toStringWithTabs;
import static java.nio.charset.StandardCharsets.UTF_8;

public class IOUtils {

	@SneakyThrows
	public static JsonObject readJson(Path file) {
		return JsonParser.parseString(String.join("", Files.readAllLines(file))).getAsJsonObject();
	}

	public static void fileWrite(String file, BiConsumer<BufferedWriter, List<String>> consumer) {
		write(file, List.of(), writer -> {
			final List<String> outputs = new ArrayList<>();
			consumer.accept(writer, outputs);
			writer.write(String.join(System.lineSeparator(), outputs));
		});
	}

	private static void write(String fileName, List<StandardOpenOption> openOptions, UncheckedConsumer<BufferedWriter> consumer) {
		try {
			final Path path = Paths.get(fileName);
			final File file = path.toFile();
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}

			final StandardOpenOption[] options = openOptions.toArray(StandardOpenOption[]::new);
			try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8, options)) {
				consumer.accept(writer);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void fileWrite(String file, JsonObject json) {
		IOUtils.fileWrite(file, (writer, outputs) ->  outputs.add(toStringWithTabs(json)));
	}

	@SneakyThrows
	public static void forEach(String directory, String extension, Consumer<Path> consumer) {
		try (var walker = Files.walk(Path.of(directory))) {
			walker.forEach(path -> {
				final String uri = path.toUri().toString();
				try {
					if (uri.endsWith(extension))
						consumer.accept(path);
				} catch (Exception e){
					System.out.println("Error on path: " + uri);
					e.printStackTrace();
				}
			});
		}
	}

	public static void modifyJson(Path file, Consumer<JsonObject> consumer) {
		JsonObject json = IOUtils.readJson(file);
		var before = json.toString();
		consumer.accept(json);
		var after = json.toString();
		if (before.equals(after))
			return;
		IOUtils.fileWrite(file.toString(), json);
	}
}
