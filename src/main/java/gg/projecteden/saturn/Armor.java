package gg.projecteden.saturn;

import gg.projecteden.saturn.utils.IOUtils;
import gg.projecteden.saturn.utils.ImageUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Armor {
	final List<String> SLOTS = List.of("boots", "chestplate", "helmet", "leggings");
	final List<String> ORDER = List.of(
			"wither", "warden", "berserker", "brown_berserk", "copper", "cobalt", "druid", "hellfire", "jarl",
			"mythril", "tank", "thor", "wizard", "wolf", "fishing"
	);

	final String MODEL_TEMPLATE = """
		{
			"parent": "projecteden/items/armor/template_armor",
			"textures": {
				"0": "projecteden/items/armor/%s/%s"
			}
		}
	""";

	final String OVERRIDE_TEMPLATE = """
		{"predicate": {"custom_model_data": %s}, "model": "projecteden/items/armor/%s/%s"}
	""";

	private static final Path MODELS_PATH = Paths.get("src/main/resources/armor/templates/models");
	private static final File TEXTURES_FOLDER = Paths.get("assets/minecraft/textures/projecteden/items/armor").toFile();

	private static final String ITEM_MODEL_OUTPUT_PATH = "assets/minecraft/models/item/leather_%s.json";
	private static final File LAYER_OUTPUT_FOLDER = Paths.get("assets/minecraft/textures/models/armor").toFile();
	private static final String MODEL_OUTPUT_PATH = "assets/minecraft/models/projecteden/items/armor/%s/%s.json";

	@Test
	@SneakyThrows
	void run() {
		generateLeatherLayers();
		createModelFiles();
		createOverrides();
		printEnumValues();
	}

	private void generateLeatherLayers() {
		for (var layer : List.of(1, 2)) {
			var layerFile = "layer_%s.png".formatted(layer);
			var combined = ImageUtils.newImage(64, (ORDER.size() + 1) * 32);
			var leather = ImageUtils.read(TEXTURES_FOLDER, "leather/" + layerFile);
			var graphics = combined.getGraphics();
			graphics.drawImage(leather, 0, 0, null);

			var index = 1;
			for (var type : ORDER) {
				var image = ImageUtils.read(TEXTURES_FOLDER, type + "/" + layerFile);
				graphics.drawImage(image, 0, 32 * index++, null);
			}

			ImageUtils.write(combined, LAYER_OUTPUT_FOLDER, "leather_" + layerFile);
		}
	}

	private void createModelFiles() {
		for (var type : ORDER)
			for (var slot : SLOTS)
				IOUtils.fileWrite(MODEL_OUTPUT_PATH.formatted(type, slot), (writer, outputs) ->
						outputs.add(MODEL_TEMPLATE.formatted(type, slot)));
	}

	private void createOverrides() throws IOException {
		for (var slot : SLOTS) {
			var modelId = 1;
			var overrides = new ArrayList<String>();
			for (var type : ORDER)
				overrides.add(OVERRIDE_TEMPLATE.formatted(modelId++, type, slot));
			final String template = Files.readString(MODELS_PATH.resolve("leather_" + slot + ".json")).replace("__OVERRIDES__", String.join(",\n\t\t", overrides));
			final Path path = Paths.get(ITEM_MODEL_OUTPUT_PATH.formatted(slot));
			path.toFile().getParentFile().mkdirs();
			Files.write(path, template.getBytes(), CREATE, TRUNCATE_EXISTING);
		}
	}

	private void printEnumValues() {
		var modelId = 1;
		System.out.println("CustomArmorType enum:");
		for (var type : ORDER)
			System.out.printf("\t%s(%d),%n", type.toUpperCase(), modelId++);
	}

}
