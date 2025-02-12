package gg.projecteden.saturn;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CustomMaterialGenerator {

    private static final String ITEMS_DIR = "assets/minecraft/items";
    private static final String[] additions = {
            "NULL(\"misc/null\", Material.PAPER),",
            "NOTE_BLOCK(\"customblocks/misc/note_block\"),",
            "TRIPWIRE(\"customblocks/misc/tripwire\"),"
    };

    @Test
    @SneakyThrows
    public void generate() {
        for (String addition : additions)
            System.out.println(addition);

        // Fetch and parse the CustomMaterial enum from GitHub
        List<CustomMaterialParser.CustomMaterialEntry> customMaterials = CustomMaterialParser.fetchAndParse();

        try (Stream<Path> paths = Files.walk(Paths.get(ITEMS_DIR))) {
            final String[] currentFolder = {null}; // To track the current top-level folder
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            // Get the relative path from the base path
                            Path relativePath = Paths.get(ITEMS_DIR).relativize(path);
                            String relativePathStr = relativePath.toString().replace("\\", "/").replace(".json", ""); // Normalize to forward slashes

                            // Split the path into parts
                            String[] parts = relativePathStr.split("/", 2); // Split into top-level folder and the rest

                            // Update and print the folder comment if it's a new folder
                            if (currentFolder[0] == null || !currentFolder[0].equals(parts[0])) {
                                currentFolder[0] = parts[0];
                                System.out.println("// " + currentFolder[0]); // Print folder comment
                            }

                            // Read the JSON file and extract old_base_material and old_custom_model_data
                            String jsonContent = Files.readString(path);
                            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

                            String oldBaseMaterial = jsonObject.has("old_base_material") ? jsonObject.get("old_base_material").getAsString().toUpperCase() : null;
                            int oldCustomModelData = jsonObject.has("old_custom_model_data") ? jsonObject.get("old_custom_model_data").getAsInt() : -1;

                            // Find the matching enum name
                            String enumName = null;
                            for (CustomMaterialParser.CustomMaterialEntry entry : customMaterials) {
                                if (entry.getMaterial().equals(oldBaseMaterial) && entry.getCustomModelData() == oldCustomModelData) {
                                    enumName = entry.getEnumName();
                                    break;
                                }
                            }

                            if (enumName == null) {
                                // Generate the enum name if no match is found
                                String withoutFirstFolder = parts[1]; // Exclude the first folder in the name
                                enumName = withoutFirstFolder.replace("/", "_").replace(".json", "").toUpperCase(); // Replace slashes and convert to uppercase
                            }

                            // skip things that end in UUIDS
                            if (enumName.matches("^.*[A-Z]+.*([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$"))
                                return;

                            if (enumName.startsWith("8BIT"))
                                enumName = enumName.replace("8BIT", "EIGHT_BIT");

                            // Adjust the constructor parameter based on old_base_material
                            String constructorValue;
                            if ("PAPER".equals(oldBaseMaterial) || oldBaseMaterial == null) {
                                constructorValue = String.format("\"%s\"", relativePathStr);
                            } else if ("LEATHER_HORSE_ARMOR".equals(oldBaseMaterial)) {
                                constructorValue = String.format("\"%s\", true", relativePathStr);
                            } else {
                                constructorValue = String.format("\"%s\", Material.%s", relativePathStr, oldBaseMaterial);
                            }

                            System.out.printf(" %s(%s),%n", enumName, constructorValue);
                        } catch (IOException e) {
                            System.err.println("Error reading JSON file: " + path);
                        }
                    });
        }
    }

    @Test
    @SneakyThrows
    public void find() {
        int targetModelData = 8500;
        find(targetModelData);
    }

    @SneakyThrows
    public void find(int targetModelData) {
        String basePath = "assets/minecraft/items";

        try (Stream<Path> paths = Files.walk(Paths.get(basePath))) {
            final boolean[] found = {false}; // To track if a match is found

            paths.filter(Files::isRegularFile) // Only regular files
                    .filter(path -> path.toString().endsWith(".json")) // Only JSON files
                    .forEach(path -> {
                        // Read the JSON file and check 'old_custom_model_data'
                        try {
                            String jsonContent = Files.readString(path);
                            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                            if (jsonObject.has("old_custom_model_data") &&
                                    jsonObject.get("old_custom_model_data").getAsInt() == targetModelData) {
                                // Get the relative path from the base path
                                Path relativePath = Paths.get(basePath).relativize(path);
                                String relativePathStr = relativePath.toString().replace("\\", "/"); // Normalize slashes

                                // Generate the enum name
                                String withoutExtension = relativePathStr.substring(0, relativePathStr.lastIndexOf('.')); // Remove extension
                                String withoutFirstFolder = withoutExtension.split("/", 2)[1]; // Exclude the first folder
                                String enumName = withoutFirstFolder.replace("/", "_").toUpperCase(); // Format enum name

                                System.out.println("Match found: " + enumName);
                                found[0] = true;
                            }
                        } catch (IOException e) {
                            System.err.println("Error reading JSON file: " + path);
                        }
                    });

            if (!found[0]) {
                System.out.println("No match found for 'old_custom_model_data' = " + targetModelData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @SneakyThrows
    public void findNewEnumName() {
        String enumName = "DECORATION_CATALOG_MASTER";

        // Get the old_custom_model_data from the GitHub enum file
        int oldCustomModelData = findByEnumName(enumName);
        if (oldCustomModelData == -1) {
            System.err.println("No old_custom_model_data found for enum: " + enumName);
            return;
        }

        // Use the existing find method to get the new enum name
        find(oldCustomModelData);
    }

    @SneakyThrows
    public int findByEnumName(String enumName) {
        // URL of the enum file on GitHub
        String fileUrl = "https://raw.githubusercontent.com/ProjectEdenGG/Nexus/master/src/main/java/gg/projecteden/nexus/features/resourcepack/models/CustomMaterial.java";

        try {
            // Fetch the file content from the GitHub URL
            String fileContent = fetchFileContent(fileUrl);
            if (fileContent == null) {
                throw new IOException("Failed to fetch file content");
            }

            // Search for the enum name and extract the int value
            String[] lines = fileContent.split("\n");
            for (String line : lines) {
                if (line.trim().startsWith(enumName + "(")) { // Find the enum line
                    String constructorArgs = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                    String[] args = constructorArgs.split(",");

                    for (String arg : args) {
                        arg = arg.trim();
                        if (arg.matches("\\d+")) { // Match a single integer
                            return Integer.parseInt(arg);
                        }
                    }
                }
            }

            throw new IllegalArgumentException("Enum name not found or no integer value in constructor: " + enumName);
        } catch (Exception e) {
            System.err.println("Error processing enum file: " + e.getMessage());
            return -1; // Return -1 to indicate failure
        }
    }

    private String fetchFileContent(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\A");
            String content = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
            return content;
        } else {
            System.err.println("Failed to fetch file. HTTP response code: " + responseCode);
            return null;
        }
    }

    private int extractOldCustomModelData(String fileContent) throws Exception {
        // Look for the old_custom_model_data field in the file content
        int startIndex = fileContent.indexOf("old_custom_model_data");
        if (startIndex == -1) {
            throw new Exception("old_custom_model_data not found in file");
        }

        // Extract the value (assumes "old_custom_model_data" is followed by a colon and a number)
        String substring = fileContent.substring(startIndex);
        int colonIndex = substring.indexOf(":");
        int commaIndex = substring.indexOf(",", colonIndex); // Find the end of the value
        String value = substring.substring(colonIndex + 1, commaIndex).trim();

        return Integer.parseInt(value);
    }

    public static class CustomMaterialParser {

        private static final String CUSTOM_MATERIAL_URL = "https://raw.githubusercontent.com/ProjectEdenGG/Nexus/master/src/main/java/gg/projecteden/nexus/features/resourcepack/models/CustomMaterial.java";

        public static List<CustomMaterialEntry> fetchAndParse() throws IOException {
            List<CustomMaterialEntry> entries = new ArrayList<>();
            URL url = new URL(CUSTOM_MATERIAL_URL);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String line;
                Pattern pattern = Pattern.compile("(\\w+)\\(Material\\.(\\w+),\\s*(\\d+)(?:,\\s*true)?\\)");
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String enumName = matcher.group(1);
                        String material = matcher.group(2);
                        int customModelData = Integer.parseInt(matcher.group(3));
                        entries.add(new CustomMaterialEntry(enumName, material, customModelData));
                    }
                }
            }
            return entries;
        }

        public static class CustomMaterialEntry {
            private final String enumName;
            private final String material;
            private final int customModelData;

            public CustomMaterialEntry(String enumName, String material, int customModelData) {
                this.enumName = enumName;
                this.material = material;
                this.customModelData = customModelData;
            }

            public String getEnumName() {
                return enumName;
            }

            public String getMaterial() {
                return material;
            }

            public int getCustomModelData() {
                return customModelData;
            }
        }
    }


}
