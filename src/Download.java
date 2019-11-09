import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Download {

    public static void download(String link, ArrayList<String> fileNames, String folderForSave) {
        LocalDateTime startTime = LocalDateTime.now();

        System.out.println("Загружается файл: " + fileNames.get(0) + " потоком " + Thread.currentThread().getName());

        URL website;
        try {
            website = new URL(link);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Неверный формат URL:" + System.lineSeparator() +
                    link +System.lineSeparator() +
                    "файл не будет скачан");
        }

        File file = new File(folderForSave + File.separator + fileNames.get(0));
        try (InputStream in = website.openStream()) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке. " + System.lineSeparator() +
                    "Сыылка: " + link + System.lineSeparator() +
                    "Файл: " + file + " не был загружен");
        }

        System.out.println("Файл: " + fileNames.get(0) + " загружен: " +
                Utils.getMB(file.length()) + " МБ за " +
                Utils.getDuration(startTime) + " сек");

        if (fileNames.size() > 1) {
            for (int i = 1; i < fileNames.size(); i++) {
                System.out.println("Файл: " + fileNames.get(0) + " копируется в: " + fileNames.get(i));
                startTime = LocalDateTime.now();

                try {
                    Files.copy(file.toPath(),
                            Paths.get(folderForSave + File.separator + fileNames.get(i)),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Ошибка при копировании из " + file +System.lineSeparator()+
                            " файл: " + fileNames.get(i) + " не был загружен");

                }

                System.out.println("Файл: " + fileNames.get(i) + " скопирован: " +
                        Utils.getMB(file.length()) + " МБ за " +
                        Utils.getDuration(startTime) + " сек");
            }
        }
    }
}
