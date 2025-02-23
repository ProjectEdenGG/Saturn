package gg.projecteden.saturn.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class IOUtils {

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
}
