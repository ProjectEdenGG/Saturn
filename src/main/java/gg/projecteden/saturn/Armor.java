package gg.projecteden.saturn;

import gg.projecteden.saturn.utils.IOUtils;
import gg.projecteden.saturn.utils.ImageUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Armor {
	final List<String> SLOTS = List.of("boots", "chestplate", "helmet", "leggings");
	final List<String> ORDER = List.of(
			"wither", "warden", "berserker", "brown_berserk", "copper", "cobalt", "druid", "hellfire", "jarl",
			"mythril", "tank", "thor", "wizard", "wolf", "fishing"
	);

	final String MODEL_TEMPLATE = """
	{
		"parent": "projecteden/items/skins/armor/template_armor",
		"textures": {
			"0": "projecteden/items/skins/armor/%s/%s"
		}
	}
	""";

	final String BIG_MODEL_TEMPLATE = """
	{
		"parent": "projecteden/ui/gui/tool_modification/template_icon_big",
		"textures": {
			"skin": "projecteden/items/skins/armor/%s/%s"
		}
	}
	""";

	final String ITEM_TEMPLATE = """
	{
		"old_base_material": "leather_%s",
		"old_custom_model_data": %d,
		"model": {
			"type": "minecraft:model",
			"model": "projecteden/items/skins/armor/%s/%s",
			"tints": []
		}
	}
	""";

	final String BIG_ITEM_TEMPLATE = """
	{
		"model": {
			"type": "minecraft:model",
			"model": "projecteden/items/skins/armor/%s/big/%s",
			"tints": []
		}
	}
	""";

	private static final File TEXTURES_FOLDER = Paths.get("assets/minecraft/textures/projecteden/items/skins/armor").toFile();

	private static final File LAYER_1_OUTPUT_FOLDER = Paths.get("assets/minecraft/textures/entity/equipment/humanoid").toFile();
	private static final File LAYER_2_OUTPUT_FOLDER = Paths.get("assets/minecraft/textures/entity/equipment/humanoid_leggings").toFile();

	private static final String MODEL_OUTPUT_PATH = "assets/minecraft/models/projecteden/items/skins/armor/%s/%s.json";
	private static final String BIG_MODEL_OUTPUT_PATH = "assets/minecraft/models/projecteden/items/skins/armor/%s/big/%s.json";
	private static final String ITEM_OUTPUT_PATH = "assets/minecraft/items/skins/armor/%s/%s.json";
	private static final String BIG_ITEM_OUTPUT_PATH = "assets/minecraft/items/skins/armor/%s/big/%s.json";

	@Test
	@SneakyThrows
	void run() {
		generateLeatherLayers();
		createItemAndModelFiles();
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

			File output = layer == 1 ? LAYER_1_OUTPUT_FOLDER : LAYER_2_OUTPUT_FOLDER;
			ImageUtils.write(combined, output, "leather.png");
		}
	}

	private void createItemAndModelFiles() {
		var index = new AtomicInteger(1);
		for (var type : ORDER) {
			for (var slot : SLOTS) {
				IOUtils.fileWrite(MODEL_OUTPUT_PATH.formatted(type, slot), (writer, outputs) -> outputs.add(MODEL_TEMPLATE.formatted(type, slot)));
				IOUtils.fileWrite(BIG_MODEL_OUTPUT_PATH.formatted(type, slot), (writer, outputs) -> outputs.add(BIG_MODEL_TEMPLATE.formatted(type, slot)));
				IOUtils.fileWrite(ITEM_OUTPUT_PATH.formatted(type, slot), (writer, outputs) -> outputs.add(ITEM_TEMPLATE.formatted(slot, index.get(), type, slot)));
				IOUtils.fileWrite(BIG_ITEM_OUTPUT_PATH.formatted(type, slot), (writer, outputs) -> outputs.add(BIG_ITEM_TEMPLATE.formatted(type, slot)));
			}
			index.getAndIncrement();
		}
	}

	private void printEnumValues() {
		System.out.println("public enum CustomArmorType {");
		for (var type : ORDER)
			System.out.printf("\t%s(\"skins/armor/%s\"),%n", type.toUpperCase(), type.toLowerCase());
		System.out.println("\t;");
	}

}
