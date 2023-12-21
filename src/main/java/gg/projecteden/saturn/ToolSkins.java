package gg.projecteden.saturn;

import gg.projecteden.saturn.utils.IOUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ToolSkins {

	private static final List<String> SKINS = List.of(
			"adamantite", "copper", "damascus", "hellfire", "mythril", "vines", "amythest", "sculk", "cherry",
			"mechanical"
	);

	private static final List<String> MATERIALS = List.of(
			"wooden", "stone", "iron", "golden", "diamond", "netherite"
	);

	private static final List<String> TOOLS = List.of(
			"shovel", "axe", "hoe", "pickaxe", "sword"
	);

	private static final List<String> BOW_TEXTURES = List.of(
			"bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2"
	);

	private static final List<String> CROSSBOW_TEXTURES = List.of(
			"crossbow_arrow", "crossbow_firework", "crossbow_standby",
			"crossbow_pulling_0", "crossbow_pulling_1", "crossbow_pulling_2"
	);

	private static final String MATERIAL_TEMPLATE = """
	{
		"parent": "minecraft:item/handheld",
		"textures": {
			"layer0": "minecraft:item/%s_%s"
		},
		"overrides": [
			%s
		]
	}
	""";

	private static final String OVERRIDE_TEMPLATE = """
			{"predicate": {"custom_model_data": %s}, "model": "projecteden/items/skins/%s/%s"}""";

	private static final String HANDHELD_MODEL_TEMPLATE = """
	{
		"parent": "minecraft:item/handheld",
		"textures": {
			"layer0": "projecteden/items/skins/%s/%s"
		}
	}
	""";

	private static final String BOW_MODEL_TEMPLATE = """
	{
		"parent": "minecraft:item/bow",
		"textures": {
			"layer0": "projecteden/items/skins/%s/%s"
		}
	}
	""";

	private static final String CROSSBOW_MODEL_TEMPLATE = """
	{
		"parent": "item/crossbow",
		"textures": {
			"layer0": "projecteden/items/skins/%s/%s"
		}
	}
	""";
	
	private static final String MODELS_PATH = "assets/minecraft/models/projecteden/items/skins/%s/%s.json";
	private static final String ITEMS_PATH = "assets/minecraft/models/item/%s_%s.json";

	@Test
	void run() {
		for (String skin : SKINS) {
			for (String tool : TOOLS) {
				var file = MODELS_PATH.formatted(skin, tool);
				IOUtils.fileWrite(file, (writer, outputs) -> outputs.add(HANDHELD_MODEL_TEMPLATE.formatted(skin, tool)));
			}

			for (String bowTexture : BOW_TEXTURES) {
				var file = MODELS_PATH.formatted(skin, bowTexture);
				IOUtils.fileWrite(file, (writer, outputs) -> outputs.add(BOW_MODEL_TEMPLATE.formatted(skin, bowTexture)));
			}

			for (String bowTexture : CROSSBOW_TEXTURES) {
				var file = MODELS_PATH.formatted(skin, bowTexture);
				IOUtils.fileWrite(file, (writer, outputs) -> outputs.add(CROSSBOW_MODEL_TEMPLATE.formatted(skin, bowTexture)));
			}
		}

		for (String material : MATERIALS) {
			for (String tool : TOOLS) {
				List<String> overrides = new ArrayList<>();
				int modelId = 1000;
				for (String skin : SKINS)
					overrides.add(OVERRIDE_TEMPLATE.formatted(modelId++, skin, tool));

				IOUtils.fileWrite(ITEMS_PATH.formatted(material, tool), (writer, outputs) -> {
					final String overridesJoined = String.join(",\n\t\t", overrides);
					outputs.add(MATERIAL_TEMPLATE.formatted(material, tool, overridesJoined));
				});
			}
		}
	}
}

