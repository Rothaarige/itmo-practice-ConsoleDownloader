import javafx.util.Pair;

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

public class Downloader {
    private Analyzer analyzer;

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public void download(Pair<String, ArrayList<String>> pair, String folderForSave) {
        String link = pair.getKey();
        ArrayList<String> fileNames = pair.getValue();

        LocalDateTime startTime = LocalDateTime.now();

        analyzer.onStart(fileNames.get(0));

        URL website;
        try {
            website = new URL(link);
        } catch (MalformedURLException e) {
            analyzer.onErrors();
            throw new RuntimeException("Неверный формат URL:" + System.lineSeparator() +
                    link + System.lineSeparator() +
                    "файл не будет скачан");
        }

        File file = new File(folderForSave + File.separator + fileNames.get(0));
        try (InputStream in = website.openStream()) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            analyzer.onFinish(fileNames.get(0), Utils.getMB(file.length()), Utils.getDuration(startTime));
        } catch (IOException e) {
            analyzer.onErrors();
            throw new RuntimeException("Ошибка при загрузке. " + System.lineSeparator() +
                    "Ссылка: " + link + System.lineSeparator() +
                    "Файл: " + file + " не был загружен");
        }

        analyzer.onFinish(fileNames.get(0), Utils.getMB(file.length()), Utils.getDuration(startTime));

        if (fileNames.size() > 1) {
            for (int i = 1; i < fileNames.size(); i++) {
                analyzer.onStartCopy(fileNames.get(0), fileNames.get(i));

                startTime = LocalDateTime.now();

                try {
                    Files.copy(file.toPath(),
                            Paths.get(folderForSave + File.separator + fileNames.get(i)),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    analyzer.onErrorsCopy();
                    throw new RuntimeException("Ошибка при копировании из " + file + System.lineSeparator() +
                            " файл: " + fileNames.get(i) + " не был загружен");
                }
                analyzer.onFinishCopy(fileNames.get(i), Utils.getMB(file.length()), Utils.getDuration(startTime));
            }
        }
    }
}
