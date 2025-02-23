package gg.projecteden.saturn;

import gg.projecteden.saturn.utils.IOUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ToolSkins {

	private static final List<String> SKINS = List.of(
			"adamantite", "copper", "cobalt", "hellfire", "mythril", "vines", "amythest", "sculk", "cherry",
			"mechanical", "8bit"
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

	private static final List<String> FISHING_ROD_TEXTURES = List.of(
			"fishing_rod", "fishing_rod_cast"
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

	private static final String BOW_TEMPLATE = """
		{
			"parent": "item/generated",
			"textures": {
				"layer0": "item/bow"
			},
			"display": {
				"thirdperson_righthand": { "rotation": [ -80, 260, -40 ], "translation": [ -1, -2, 2.5 ], "scale": [ 0.9, 0.9, 0.9 ] },
				"thirdperson_lefthand": { "rotation": [ -80, -280, 40 ], "translation": [ -1, -2, 2.5 ], "scale": [ 0.9, 0.9, 0.9 ] },
				"firstperson_righthand": { "rotation": [ 0, -90, 25 ], "translation": [ 1.13, 3.2, 1.13], "scale": [ 0.68, 0.68, 0.68 ] },
				"firstperson_lefthand": { "rotation": [ 0, 90, -25 ], "translation": [ 1.13, 3.2, 1.13], "scale": [ 0.68, 0.68, 0.68 ] }
			},
			"overrides": [
				{"predicate": {"pulling": 1 }, "model": "item/bow_pulling_0"},
				{"predicate": {"pulling": 1, "pull": 0.65 }, "model": "item/bow_pulling_1"},
				{"predicate": {"pulling": 1, "pull": 0.9 }, "model": "item/bow_pulling_2"},

		%s
			]
		}
		""";

	private static final String BOW_OVERRIDES_TEMPLATE = """
			{"predicate": {"custom_model_data": __MODEL_ID__}, "model": "projecteden/items/skins/__SKIN__/bow"},
			{"predicate": {"custom_model_data": __MODEL_ID__, "pulling": 1 }, "model": "projecteden/items/skins/__SKIN__/bow_pulling_0"},
			{"predicate": {"custom_model_data": __MODEL_ID__, "pulling": 1, "pull": 0.65 }, "model": "projecteden/items/skins/__SKIN__/bow_pulling_1"},
			{"predicate": {"custom_model_data": __MODEL_ID__, "pulling": 1, "pull": 0.9 }, "model": "projecteden/items/skins/__SKIN__/bow_pulling_2"}
	""";

	private static final String CROSSBOW_TEMPLATE = """
		{
			"parent": "item/generated",
			"textures": {
				"layer0": "item/crossbow_standby"
			},
			"display": {
				"thirdperson_righthand": { "rotation": [ -90, 0, -60 ], "translation": [ 2, 0.1, -3 ], "scale": [ 0.9, 0.9, 0.9 ] },
				"thirdperson_lefthand": { "rotation": [ -90, 0, 30 ], "translation": [ 2, 0.1, -3 ], "scale": [ 0.9, 0.9, 0.9 ] },
				"firstperson_righthand": { "rotation": [ -90, 0, -55 ], "translation": [ 1.13, 3.2, 1.13], "scale": [ 0.68, 0.68, 0.68 ] },
				"firstperson_lefthand": { "rotation": [ -90, 0, 35 ], "translation": [ 1.13, 3.2, 1.13], "scale": [ 0.68, 0.68, 0.68 ] }
			},
			"overrides": [
				{"predicate": {"pulling": 1}, "model": "item/crossbow_pulling_0"},
				{"predicate": {"pulling": 1, "pull": 0.58}, "model": "item/crossbow_pulling_1"},
				{"predicate": {"pulling": 1, "pull": 1.0}, "model": "item/crossbow_pulling_2"},
				{"predicate": {"charged": 1},"model": "item/crossbow_arrow"},
				{"predicate": {"charged": 1, "firework": 1}, "model": "item/crossbow_firework"},

		%s
			]
		}
		""";

	private static final String CROSSBOW_OVERRIDES_TEMPLATE = """
			{"predicate": {"custom_model_data": __MODEL_ID__}, "model": "projecteden/items/skins/__SKIN__/crossbow_standby"},
			{"predicate": {"custom_model_data": __MODEL_ID__, "pulling": 1}, "model": "projecteden/items/skins/__SKIN__/crossbow_pulling_0"},
			{"predicate": {"custom_model_data": __MODEL_ID__, "pulling": 1, "pull": 0.58}, "model": "projecteden/items/skins/__SKIN__/crossbow_pulling_1"},
			{"predicate": {"custom_model_data": __MODEL_ID__, "pulling": 1, "pull": 1.0}, "model": "projecteden/items/skins/__SKIN__/crossbow_pulling_2"},
			{"predicate": {"custom_model_data": __MODEL_ID__, "charged": 1},"model": "projecteden/items/skins/__SKIN__/crossbow_arrow"},
			{"predicate": {"custom_model_data": __MODEL_ID__, "charged": 1, "firework": 1}, "model": "projecteden/items/skins/__SKIN__/crossbow_firework"}
	""";

	private static final String FISHING_ROD_TEMPLATE = """
		{
			"parent": "item/handheld_rod",
			"textures": {
				"layer0": "item/fishing_rod"
			},
			"overrides": [
				{"predicate": {"cast": 1}, "model": "item/fishing_rod_cast"},
				
		%s
			]
		}
		""";

	private static final String FISHING_ROD_OVERRIDES_TEMPLATE = """
			{"predicate": {"custom_model_data": __MODEL_ID__}, "model": "projecteden/items/skins/__SKIN__/fishing_rod"},
			{"predicate": {"custom_model_data": __MODEL_ID__, "cast": 1}, "model": "projecteden/items/skins/__SKIN__/fishing_rod_cast"}
	""";

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

	private static final String FISHING_ROD_MODEL_TEMPLATE = """
		{
			"parent": "item/handheld_rod",
			"textures": {
				"layer0": "projecteden/items/skins/%s/%s"
			}
		}
		""";
	
	private static final String MODELS_PATH = "assets/minecraft/models/projecteden/items/skins/%s/%s.json";
	private static final String ITEMS_PATH = "assets/minecraft/models/item/%s.json";

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

			for (String fishingRodTexture : FISHING_ROD_TEXTURES) {
				var file = MODELS_PATH.formatted(skin, fishingRodTexture);
				IOUtils.fileWrite(file, (writer, outputs) -> outputs.add(FISHING_ROD_MODEL_TEMPLATE.formatted(skin, fishingRodTexture)));
			}
		}

		for (String material : MATERIALS) {
			for (String tool : TOOLS) {
				List<String> overrides = new ArrayList<>();
				int modelId = 1000;
				for (String skin : SKINS)
					overrides.add(OVERRIDE_TEMPLATE.formatted(modelId++, skin, tool));

				IOUtils.fileWrite(ITEMS_PATH.formatted(material + "_" + tool), (writer, outputs) -> {
					final String overridesJoined = String.join(",\n\t\t", overrides);
					outputs.add(MATERIAL_TEMPLATE.formatted(material, tool, overridesJoined));
				});
			}
		}

		IOUtils.fileWrite(ITEMS_PATH.formatted("bow"), (writer, outputs) -> {
			List<String> bows = new ArrayList<>();
			int modelId = 1000;
			for (String skin : SKINS)
				bows.add(BOW_OVERRIDES_TEMPLATE
						.replaceAll("__MODEL_ID__", String.valueOf(modelId++))
						.replaceAll("__SKIN__", skin));

			outputs.add(BOW_TEMPLATE.formatted(String.join(",\n\n", bows)).replaceAll("\n,", ",").trim());
		});

		IOUtils.fileWrite(ITEMS_PATH.formatted("crossbow"), (writer, outputs) -> {
			List<String> crossbows = new ArrayList<>();
			int modelId = 1000;
			for (String skin : SKINS)
				crossbows.add(CROSSBOW_OVERRIDES_TEMPLATE
						.replaceAll("__MODEL_ID__", String.valueOf(modelId++))
						.replaceAll("__SKIN__", skin));

			outputs.add(CROSSBOW_TEMPLATE.formatted(String.join(",\n\n", crossbows)).replaceAll("\n,", ",").trim());
		});

		IOUtils.fileWrite(ITEMS_PATH.formatted("fishing_rod"), (writer, outputs) -> {
			List<String> fishingRods = new ArrayList<>();
			int modelId = 1000;
			for (String skin : SKINS)
				fishingRods.add(FISHING_ROD_OVERRIDES_TEMPLATE
						.replaceAll("__MODEL_ID__", String.valueOf(modelId++))
						.replaceAll("__SKIN__", skin));

			outputs.add(FISHING_ROD_TEMPLATE.formatted(String.join(",\n\n", fishingRods)).replaceAll("\n,", ",").trim());
		});
	}
}

