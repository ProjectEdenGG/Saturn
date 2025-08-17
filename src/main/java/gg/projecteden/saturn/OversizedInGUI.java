package gg.projecteden.saturn;

import gg.projecteden.saturn.utils.EdenAPICompat;
import gg.projecteden.saturn.utils.IOUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static gg.projecteden.saturn.utils.IOUtils.modifyJson;

public class OversizedInGUI extends EdenAPICompat {

	@Test
	@SneakyThrows
	void oversizedInGUI() {
		IOUtils.forEach("assets/minecraft/items", ".json", file -> {
			modifyJson(file, json -> json.addProperty("oversized_in_gui", true));
		});
	}

}
