package gg.projecteden.saturn;

import gg.projecteden.saturn.utils.IOUtils;
import gg.projecteden.saturn.utils.ResourcePackOverriddenMaterial;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class ItemsConversion {

	private static final String ITEMS_PATH = "assets/minecraft/items/%s.json";
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


}
