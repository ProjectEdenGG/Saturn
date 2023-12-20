package gg.projecteden.saturn;

import gg.projecteden.saturn.utils.ImageUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class PowerOfTwo {

	static boolean isPowerOfTwo(int value) {
		return value != 0 && ((value & (value - 1)) == 0);
	}

	@Test
	@SneakyThrows
	void run() {
		String folderName = "assets/minecraft/textures";
		Path folderPath = Paths.get(folderName);
		Set<Path> textures = new HashSet<>();

		try (var walker = Files.walk(folderPath)) {
			walker.forEach(path -> {
				try {
					final String uri = path.toUri().toString();
					// ignore entity textures
					if (uri.contains("entity/"))
						return;

					if (uri.endsWith(".png")) {
						textures.add(path);
					}

				} catch (Exception ex) {
					System.out.println(path.getFileName().toString());
					ex.printStackTrace();
				}
			});
		}

		int notPower2 = 0;
		for (Path path : textures) {
			final BufferedImage texture = ImageUtils.read(path.toFile());
			int height = texture.getHeight();
			int width = texture.getWidth();
			boolean pow2 = isPowerOfTwo(height) && isPowerOfTwo(width);

			if (!pow2) {
				notPower2++;
				String subFolderName = path.toString().replace(folderName + "\\", "");
				System.out.println("(H=" + height + ", W=" + width + ") " + subFolderName);
			}
		}

		System.out.println();
		System.out.println("Total: " + textures.size());
		System.out.println("Not pow 2: " + notPower2);
	}
}
