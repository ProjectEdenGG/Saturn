package gg.projecteden.saturn;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Costumes {

	private static final String COSTUMES_ITEMS = "assets/minecraft/items/costumes/";
	private static final String COSTUMES_MODELS = "assets/minecraft/models/projecteden/costumes/";

	private static final String ITEM_TEMPLATE = """
		{
			"model": {
				"type": "minecraft:model",
				"model": "projecteden/costumes/%s/%s/%s",
				"tints": []
			},
			"oversized_in_gui": true
		}
		""";

	@Test
	@SneakyThrows
	void run() {
		Map<Path, List<String>> costumeModels = new HashMap<>();
		try (var walker = Files.walk(Path.of(COSTUMES_MODELS))) {
			walker.forEach(path -> {
				if (!path.toUri().toString().endsWith(".json"))
					return;

				String filePath = path.toAbsolutePath().toString();
				List<String> pathSplit = Arrays.stream(filePath.split("\\\\")).toList();
				String rootFolder = pathSplit.get(pathSplit.size() - 3);
				String subFolder = pathSplit.get(pathSplit.size() - 2);
				String model = pathSplit.get(pathSplit.size() - 1).replace(".json", "");
				costumeModels.put(path, List.of(rootFolder, subFolder, model));
			});
		}

		Map<Path, List<String>> costumeItems = new HashMap<>();
		try (var walker = Files.walk(Path.of(COSTUMES_ITEMS))) {
			walker.forEach(path -> {
				if (!path.toUri().toString().endsWith(".json"))
					return;

				String filePath = path.toAbsolutePath().toString();
				List<String> pathSplit = Arrays.stream(filePath.split("\\\\")).toList();
				String rootFolder = pathSplit.get(pathSplit.size() - 3);
				String subFolder = pathSplit.get(pathSplit.size() - 2);
				String model = pathSplit.get(pathSplit.size() - 1).replace(".json", "");
				costumeItems.put(path, List.of(rootFolder, subFolder, model));
			});
		}

		// --- Find missing ---
		Set<List<String>> existing = new HashSet<>(costumeItems.values());
		List<List<String>> missing = costumeModels.values().stream()
			.filter(entry -> !existing.contains(entry))
			.toList();

		System.out.println("Missing files: " + missing.size());
		for (var entry : missing) {
			String rootFolder = entry.get(0);
			String subFolder = entry.get(1);
			String model = entry.get(2);

			Path targetFolder = Path.of(COSTUMES_ITEMS, rootFolder, subFolder);
			Files.createDirectories(targetFolder);

			Path targetFile = targetFolder.resolve(model + ".json");

			String json = ITEM_TEMPLATE.formatted(rootFolder, subFolder, model);
			Files.writeString(targetFile, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

			System.out.println("Created: " + targetFile);
		}
	}
}
