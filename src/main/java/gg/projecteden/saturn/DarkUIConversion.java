package gg.projecteden.saturn;

import gg.projecteden.saturn.utils.ImageUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DarkUIConversion {

	List<String> lightHex = List.of("ffffff", "c6c6c6", "555555", "373737", "8b8b8b", "2b2b2b", "7e7e7e", "d2d2d2");
	List<String> darkHex = List.of("6b6b6b", "535353", "333333", "262626", "333333", "ffffff", "535353", "535353");

	List<Color> lightColors = new ArrayList<>();
	List<Color> darkColors = new ArrayList<>();

	@Test
	@SneakyThrows
	public void darkUI(){
		for (String hex : lightHex) {
			lightColors.add(Color.decode("#" + hex));
		}

		for (String hex : darkHex) {
			darkColors.add(Color.decode("#" + hex));
		}

		String folder = "src/main/resources/ui";
		Path folderPathLight = Paths.get(folder + "\\light");
		Path folderPathDark = Paths.get(folder + "\\dark");

		Set<Path> textures = new HashSet<>();

		try (var walker = Files.walk(folderPathLight)) {
			walker.forEach(path -> {
				try {
					final String uri = path.toUri().toString();
					if (uri.endsWith(".png")){
						textures.add(path);
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			});
		}

		for (Path path : textures) {
			BufferedImage texture = ImageUtils.read(path.toFile());
			texture = convert(texture);

			File outputFile = new File(folderPathDark + "\\" + path.getFileName().toString());
			try {
				ImageIO.write(texture, "png", outputFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		System.out.println("Converted textures to dark UI");
	}

	public BufferedImage convert(BufferedImage image){
		int width = image.getWidth();
		int height = image.getHeight();
		int RGBA;

		int ndx = 0;
		Set<String> changedPixels = new HashSet<>();
		for (Color lightColor : lightColors) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					RGBA = image.getRGB(i, j);
					Color pixelColor = new Color((RGBA >> 16) & 255, (RGBA >> 8) & 255, RGBA & 255, (RGBA >> 24) & 255);
					String pixel = i + "_" + j;
					if(changedPixels.contains(pixel))
						continue;

					if(pixelColor.equals(lightColor)) {
						image.setRGB(i, j, darkColors.get(ndx).getRGB());
						changedPixels.add(pixel);
					}
				}
			}
			ndx++;
		}

		return image;
	}


}
