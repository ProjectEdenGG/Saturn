package gg.projecteden.saturn;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class MoveMetaFiles {

    private static final String PARENT_MODEL_DIR = "assets/minecraft/models";

    @Test
    @SneakyThrows
    void run(){
        try (var walker = Files.walk(Path.of(PARENT_MODEL_DIR))) {
            walker.forEach(path -> {
                final String uri = path.toUri().toString();
                try {
                    if (uri.endsWith(".meta")){
                        moveFile(uri.replace("file:///", ""));
                    }
                } catch(Exception e){
                    System.out.println("Error on path: " + uri);
                    e.printStackTrace();
                }
            });
        }
    }

    @SneakyThrows
    void moveFile(String uri) {
        if (!new File(uri).exists()) {
            System.out.println("File not found: " + uri);
            return;
        }

        String model = uri.replace("assets/minecraft/models", "assets/minecraft/items")
                .replace("/projecteden", "")
                .replace("items/items/", "items/")
                .replace(".meta", ".json");
        if (!new File(model).exists()) {
            System.out.println("Destination not found for: " + uri + " (" + model + ")");
            return;
        }

        Files.copy(Path.of(uri), Path.of(model.replace(".json", ".meta")), StandardCopyOption.REPLACE_EXISTING);
        Files.deleteIfExists(Path.of(uri));
        System.out.println("Moved: " + uri);
    }

}
