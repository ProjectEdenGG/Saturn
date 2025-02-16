package gg.projecteden.saturn.utils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class ResourcePackOverriddenMaterial {

	private String material;
	private List<ModelOverride> overrides = new ArrayList<>();

	@Data
	public static class ModelOverride {
		private ModelPredicate predicate;
		private String model;

		@Data
		public static class ModelPredicate {
			@SerializedName("custom_model_data")
			private int modelId;
		}

		public String getNewFilePath(){
			String folderPath = getFolderPath();
			if (folderPath == null || folderPath.isEmpty())
				folderPath = "misc";
			else if (folderPath.equalsIgnoreCase("items"))
				folderPath = "misc";
			else if(folderPath.startsWith("items/"))
				folderPath = folderPath.replaceFirst("items/", "");

			return folderPath + "/" + this.getFileName();
		}

		public String getFolderPath() {
			String path = model.replaceFirst("projecteden/", "");
			List<String> folders = new ArrayList<>(Arrays.asList(path.split("/")));
			folders.remove(folders.size() - 1); // remove file name

			return String.join("/", folders);
		}

		public String getFileName() {
			return listLast(model, "/");
		}

		private static String listLast(String string, String delimiter) {
			return string.substring(string.lastIndexOf(delimiter) + 1);
		}
	}

	public static ResourcePackOverriddenMaterial of(Path path) {
		ResourcePackOverriddenMaterial model = getCustomModelMaterial(path);
		model.setMaterial(getMaterial(path));
		return model;
	}

	@SneakyThrows
	private static ResourcePackOverriddenMaterial getCustomModelMaterial(Path path) {
		String json = String.join("", Files.readAllLines(path));
		return new Gson().fromJson(json, ResourcePackOverriddenMaterial.class);
	}

	@Nullable
	private static String getMaterial(Path path) {
		return path.getName(path.getNameCount() - 1).toString().split("\\.")[0];
	}

}
