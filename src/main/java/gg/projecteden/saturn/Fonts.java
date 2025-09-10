package gg.projecteden.saturn;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;

public class Fonts {
	//
	private static final String FONT_FILE_NAME = "cottontail";
	private static final String FONT_FOLDER_BRANDING = "E:/workspace/branding/saturn/font/";
	//

	private static final String FOLDER_PSD = "src/main/resources/fonts/";
	private static final String BUILD_FOLDER = FOLDER_PSD + "build/";

	@Test
	@SneakyThrows
	void run() {
		System.out.println("Extracting layers using python psd-tools...");

		List<File> exportedFiles = saveLayersAsImages();
		System.out.println("Created " + exportedFiles.size() + " images");

		File output = concatenateHorizontally(exportedFiles, new File(FOLDER_PSD + FONT_FILE_NAME + ".png"));
		deleteFiles(exportedFiles);

		System.out.println("Saved font file: " + output.getAbsolutePath());
	}

	private static void deleteFiles(List<File> files) {
		for (File f : files) {
			if (f.exists()) {
				boolean deleted = f.delete();
				if (!deleted) {
					System.out.println("Failed to delete: " + f.getAbsolutePath());
				}
			}
		}
	}

	@SneakyThrows
	private static List<File> saveLayersAsImages() {
		String psdFile = FONT_FOLDER_BRANDING + FONT_FILE_NAME + ".psd";

		ProcessBuilder pb = new ProcessBuilder("python", "psd_layer_extract.py", psdFile, BUILD_FOLDER);
		pb.redirectErrorStream(true);
		Process process = pb.start();


		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line); // print all output and errors
			}
		}

		int exitCode = process.waitFor();
		if (exitCode != 0) {
			throw new RuntimeException("Python failed with exit code " + exitCode);
		}

		File[] files = new File(BUILD_FOLDER).listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
		List<File> exportedFiles = files == null ? List.of() : List.of(files);
		if (exportedFiles.isEmpty())
			throw new RuntimeException("No layers were saved");

		return exportedFiles;
	}

	@SneakyThrows
	private static File concatenateHorizontally(List<File> _images, File output) {
		List<File> images = _images.stream()
			.sorted(Comparator.comparing(File::getName, Fonts::customCompare))
			.toList();

		// Load all images into memory
		BufferedImage[] bufferedImages = new BufferedImage[images.size()];
		int totalWidth = 0;
		int maxHeight = 0;

		for (int i = 0; i < images.size(); i++) {
			bufferedImages[i] = ImageIO.read(images.get(i));
			totalWidth += bufferedImages[i].getWidth();
			maxHeight = Math.max(maxHeight, bufferedImages[i].getHeight());
		}

		// Create the final combined image
		BufferedImage combined = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);

		// Draw images side by side
		Graphics2D g = combined.createGraphics();
		int xOffset = 0;
		for (BufferedImage bi : bufferedImages) {
			g.drawImage(bi, xOffset, 0, null);
			xOffset += bi.getWidth();
		}
		g.dispose();

		// Save as PNG
		ImageIO.write(combined, "PNG", output);

		return output;
	}

	private static int customCompare(String s1, String s2) {
		int len = Math.min(s1.length(), s2.length());
		for (int i = 0; i < len; i++) {
			char c1 = s1.charAt(i);
			char c2 = s2.charAt(i);

			int order1 = charOrder(c1);
			int order2 = charOrder(c2);

			if (order1 != order2) return order1 - order2;
			if (c1 != c2) return c1 - c2;
		}
		return s1.length() - s2.length();
	}

	private static int charOrder(char c) {
		if (Character.isLetter(c)) return 1;
		if (Character.isDigit(c)) return 2;
		if (c == '_') return 3;
		return 4;
	}


}
