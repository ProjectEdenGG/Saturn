package gg.projecteden.saturn;

import gg.projecteden.saturn.utils.EdenAPICompat;
import gg.projecteden.saturn.utils.IOUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static gg.projecteden.saturn.utils.IOUtils.modifyJson;

public class OversizedInGUI extends EdenAPICompat {

	private static final String ITEMS_PATH_DIR = "assets/minecraft/items";

	@Test
	@SneakyThrows
	void oversizedInGUI() {
		IOUtils.forEach(ITEMS_PATH_DIR, ".json", file -> {
			modifyJson(file, json -> json.addProperty("oversized_in_gui", true));
		});
	}

}
