import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Download {

    public static void download(String link, ArrayList<String> fileNames, String folderForSave) throws IOException {
        LocalDateTime startTime = LocalDateTime.now();

        System.out.println("Загружается файл: " + fileNames.get(0));
        URL website = new URL(link);
        File file = new File(folderForSave + "/" + fileNames.get(0));
        try (InputStream in = website.openStream()) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("Файл: " + fileNames.get(0) + " загружен: " +
                Utils.getMB(file.length()) + " МБ за " +
                Utils.getDuration(startTime) + " сек");

        if (fileNames.size() > 1) {
            for (int i = 1; i < fileNames.size(); i++) {
                System.out.println("Файл: " + fileNames.get(0) + " копируется в: " + fileNames.get(i));
                startTime = LocalDateTime.now();

                Files.copy(Paths.get(folderForSave + "/" + fileNames.get(0)),
                        Paths.get(folderForSave + "/" + fileNames.get(i)),
                        StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Файл: " + fileNames.get(i) + " скопирован: " +
                        Utils.getMB(file.length()) + " МБ за " +
                        Utils.getDuration(startTime) + " сек");
            }
        }
    }
}
