import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static final String ERROR_MESSAGE = "Данные должны быть вида:" + System.lineSeparator() +
            "-------------------------" + System.lineSeparator() +
            "N PathFile FolderForSave" + System.lineSeparator() +
            "-------------------------" + System.lineSeparator() +
            "где N - количество одновременно качающих потоков" + System.lineSeparator() +
            "PathFile - путь к файлу со списком ссылок" + System.lineSeparator() +
            "FolderForSave - имя папки, куда складывать скаченные файлы";
    private static int numbersTread = 0;

    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.now();
        try {
            checkParameters(args);

            ConcurrentLinkedQueue<Pair<String, ArrayList<String>>> filesForSave = new Parser().parse(args[1]);
            Analyzer analyzer = new Analyzer();

            CountDownLatch latch = new CountDownLatch(numbersTread);

            for (int i = 0; i < numbersTread; i++) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Pair<String, ArrayList<String>> pr;
                        while ((pr = filesForSave.poll()) != null) {
                            try {
                                Downloader dl = new Downloader();
                                dl.setAnalyzer(analyzer);
                                dl.download(pr, args[2]);
                            } catch (RuntimeException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        System.out.println("Поток " + Thread.currentThread().getName() + " завершил работу");
                        latch.countDown();
                    }
                }).start();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                //ignore
            }

            analyzer.getResult(Utils.getDuration(startTime));

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void checkParameters(String[] args) {
        //Проверка входных данных
        if (args.length != 3) {
            throw new RuntimeException("Введены некорректные данные." + System.lineSeparator() + ERROR_MESSAGE);
        }
        //Проверка первого аргумента - количества потоков
        try {
            numbersTread = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Неверно введено количесвто потоков." + System.lineSeparator() + ERROR_MESSAGE);
        }
        //Проверка второго аргумента - файла с данными
        if (!Files.exists(Paths.get(args[1]))) {
            throw new RuntimeException("Файл: " + args[1] + " отсутствует." + System.lineSeparator() + ERROR_MESSAGE);
        }
        //Проверка третьего аргумента - папки для копирования
        if (!Files.isDirectory(Paths.get(args[2]))) {
            try {
                Files.createDirectories(Paths.get(args[2]));
            } catch (IOException e) {
                throw new RuntimeException("Что-то пошло не так при попытке создания папки для сохранения" +
                        System.lineSeparator() + args[2] + System.lineSeparator() +
                        "Возможно у вас нет прав для создания данного каталога или ошибка в имени пути.");
            }
        }
    }
}
