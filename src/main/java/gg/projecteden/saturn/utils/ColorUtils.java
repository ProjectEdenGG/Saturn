package gg.projecteden.saturn.utils;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ColorUtils {

	public static String hex(Color color) {
		return "#" + Integer.toHexString(color.getRGB()).substring(2);
	}

	public static Color average(List<Color> colors) {
		List<Integer> red = new ArrayList<>();
		List<Integer> green = new ArrayList<>();
		List<Integer> blue = new ArrayList<>();

		for (Color color : colors) {
			red.add(color.getRed());
			green.add(color.getGreen());
			blue.add(color.getBlue());
		}

		final int redAverage = (int) red.stream().mapToInt(Integer::valueOf).average().orElse(0);
		final int greenAverage = (int) green.stream().mapToInt(Integer::valueOf).average().orElse(0);
		final int blueAverage = (int) blue.stream().mapToInt(Integer::valueOf).average().orElse(0);

		return new Color(redAverage, greenAverage, blueAverage);
	}

	@Test
	void rgbinfo() {
		final Color light = Color.decode("#177889");
		final Color dark = Color.decode("#157687");
		final Color average = average(List.of(light, dark));
		final Function<Color, String> info = color -> String.format("%s / %s %s %s / %s",
				color.getRGB(), color.getRed(), color.getGreen(), color.getBlue(), hex(color));

		System.out.println("Light: " + info.apply(light));
		System.out.println("Dark: " + info.apply(dark));
		System.out.println("Average: " + info.apply(average));
		System.out.println("Diff: " + (light.getRGB() - dark.getRGB()));
	}

}
