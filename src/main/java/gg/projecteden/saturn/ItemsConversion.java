package gg.projecteden.saturn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import gg.projecteden.saturn.utils.IOUtils;
import gg.projecteden.saturn.utils.ResourcePackOverriddenMaterial;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ItemsConversion {

	private static final String ITEMS_PATH = "assets/minecraft/items/%s.json";
	private static final String ITEMS_PATH_DIR = "assets/minecraft/items";
	private static final String PARENT_MODEL_DIR = "assets/minecraft/models/item";
	private static final String PARENT_MODEL_PATH = PARENT_MODEL_DIR + "/%s.json";
	private static final String ITEM_PATH = "projecteden/%s";

	private static final String ITEM_TEMPLATE = """
		{
			"old_base_material": "%s",
			"old_custom_model_data": %s,
			"model": {
				"type": "minecraft:model",
				"model": "%s",
				"tints": []
			}
		}
		""";

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@Test
	@SneakyThrows
	void run(){
		try (var walker = Files.walk(Path.of(PARENT_MODEL_DIR))) {
			walker.forEach(path -> {
				final String uri = path.toUri().toString();
				try {
					if (uri.endsWith(".json")){
						String fileName = path.getFileName().toString().replaceAll("\\.json", "");
						writeOverrides(fileName);
					}
				} catch(Exception e){
					System.out.println("Error on path: " + uri);
					e.printStackTrace();
				}
			});
		}
	}

	private void writeOverrides(String fileName){
		Path itemPath = Path.of(PARENT_MODEL_PATH.formatted(fileName));

		ResourcePackOverriddenMaterial overriddenMaterial = ResourcePackOverriddenMaterial.of(itemPath);
		if(overriddenMaterial.getOverrides().isEmpty())
			return;

		for (ResourcePackOverriddenMaterial.ModelOverride override : overriddenMaterial.getOverrides()) {
			String subPath = override.getNewFilePath();
			String file = ITEMS_PATH.formatted(subPath);
			System.out.println("Generating file: " + file);

			String material = overriddenMaterial.getMaterial();
			String modelId = String.valueOf(override.getPredicate().getModelId());
			String model = ITEM_TEMPLATE.formatted(material, modelId, override.getModel());

			IOUtils.fileWrite(file, (writer, outputs) ->  outputs.add(model));
		}
	}

	@Test
	@SneakyThrows
	void updateTints() {
		try (var walker = Files.walk(Path.of(ITEMS_PATH_DIR))) {
			walker.forEach(path -> {
				final String uri = path.toUri().toString();
				try {
					if (uri.endsWith(".json")){
						updateTint(path.toString());
					}
				} catch(Exception e){
					System.out.println("Error on path: " + uri);
					e.printStackTrace();
				}
			});
		}
	}



	@SneakyThrows
	void updateTint(String fileName) {
		Path itemPath = Path.of(fileName);

		JsonObject json = JsonParser.parseString(String.join("", Files.readAllLines(itemPath))).getAsJsonObject();
		if (json.has("old_base_material")){
			String oldBaseMaterial = json.get("old_base_material").getAsString();
			if (oldBaseMaterial.equals("leather_horse_armor")){

				List<String> lines = Files.readAllLines(itemPath);

				// Modify only the line containing "tints": []
				List<String> updatedLines = lines.stream()
						.map(line -> line.contains("\"tints\": []")
								? line.replace("\"tints\": []", "\"tints\": [{\"type\": \"minecraft:dye\", \"default\": [0.627,0.396,0.250]}]")
								: line)
						.toList();
				String out = String.join("\n", updatedLines);

				IOUtils.fileWrite(itemPath.toString(), (writer, outputs) ->  outputs.add(out));
				System.out.println(out);
			}
		}
	}


}
