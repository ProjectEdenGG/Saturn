package gg.projecteden.saturn;

import gg.projecteden.saturn.utils.ImageUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PowerOfTwo {

	static boolean isPowerOfTwo(int value) {
		if(value < 16)
			return false;

		return (value & value - 1) == 0;
	}

	public boolean ignorePath(String uri){
		if (uri.contains("entity/")) // ignore entity textures
			return true;

		if (!uri.contains("textures/projecteden")) // ignore default textures
			return true;

		return false;
	}

	@Test
	@SneakyThrows
	void run() {
		String folderName = "assets/minecraft/textures";
		Path folderPath = Paths.get(folderName);
		Set<Path> textures = new HashSet<>();
		Set<String> animated = new HashSet<>();

		try (var walker = Files.walk(folderPath)) {
			walker.forEach(path -> {
				try {
					final String uri = path.toUri().toString();

					if (ignorePath(uri))
						return;

					if (uri.endsWith(".png")) {
						textures.add(path);
					}

					if (uri.endsWith(".png.mcmeta")){
						animated.add(path.toString().replaceAll("\\.mcmeta", ""));
					}

				} catch (Exception ex) {
					System.out.println(path.getFileName().toString());
					ex.printStackTrace();
				}
			});
		}

		// remove animated textures
		Set<Path> finalTextures = new HashSet<>();
		for (Path path : textures) {
			if(animated.contains(path.toString()))
				continue;

			finalTextures.add(path);
		}

		int notPower2 = 0;
		List<Path> sortedTextures = finalTextures.stream().sorted().toList();
		for (Path path : sortedTextures) {
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

	@Test
	@SneakyThrows
	void resize() {
		String folderName = "assets/minecraft/textures/projecteden/font";
		Path folderPath = Paths.get(folderName);

		try (var walker = Files.walk(folderPath)) {
			walker.forEach(path -> {
				try {
					final String uri = path.toUri().toString();

					if (!uri.endsWith(".png")) {
						return;
					}

					var texture = ImageUtils.read(path.toFile());
					int height = texture.getHeight();
					int width = texture.getWidth();
					System.out.println(height + " " + width + " " + uri);

					if (isPowerOfTwo(height) && isPowerOfTwo(width))
						return;

					int newHeight = 2;
					int newWidth = 2;

					while (height > newHeight)
						newHeight *= 2;

					while (width > newWidth)
						newWidth *= 2;

					System.out.println(newHeight + " " + newWidth);

					final BufferedImage resized = ImageUtils.newImage(newWidth, newHeight);
					final Graphics2D graphics = resized.createGraphics();
					graphics.drawImage(texture, 0, 0, null);
					ImageUtils.write(resized, path.toFile());
				} catch (Exception ex) {
					System.out.println(path.getFileName().toString());
					ex.printStackTrace();
				}
			});
		}
	}
}
