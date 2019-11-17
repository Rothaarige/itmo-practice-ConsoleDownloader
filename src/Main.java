import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static final String ERROR_MESSAGE = String.format("Данные должны быть вида:%n" +
            "-------------------------%n" +
            "N PathFile FolderForSave%n" +
            "-------------------------%n" +
            "где N - количество одновременно качающих потоков%n" +
            "PathFile - путь к файлу со списком ссылок%n" +
            "FolderForSave - имя папки, куда складывать скаченные файлы%n");
    private static int numbersTread = 0;
    private static final int INDEX_NUMBERS_TREAD = 0;
    private static final int INDEX_PATH_FILE = 1;
    private static final int INDEX_FOLDER_FOR_SAVE = 2;


    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.now();
        try {
            checkParameters(args);

            ConcurrentLinkedQueue<Pair<String, ArrayList<String>>> filesForSave = new Parser().parse(args[INDEX_PATH_FILE]);
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
                                dl.download(pr, args[INDEX_FOLDER_FOR_SAVE]);
                            } catch (RuntimeException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        //System.out.println("Поток " + Thread.currentThread().getName() + "завершил работу");
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
            throw new RuntimeException(String.format("Введены некорректные данные. %n%s", ERROR_MESSAGE));
        }
        //Проверка первого аргумента - количества потоков
        try {
            numbersTread = Integer.parseInt(args[INDEX_NUMBERS_TREAD]);
        } catch (NumberFormatException e) {
            throw new RuntimeException(String.format("Неверно введено количесвто потоков.%n%s", ERROR_MESSAGE));
        }
        //Проверка второго аргумента - файла с данными
        if (!Files.exists(Paths.get(args[INDEX_PATH_FILE]))) {
            throw new RuntimeException(String.format("Файл: %s отсутствует.%n%s", args[INDEX_PATH_FILE], ERROR_MESSAGE));
        }
        //Проверка третьего аргумента - папки для копирования
        if (!Files.isDirectory(Paths.get(args[INDEX_FOLDER_FOR_SAVE]))) {
            try {
                Files.createDirectories(Paths.get(args[INDEX_FOLDER_FOR_SAVE]));
            } catch (IOException e) {
                throw new RuntimeException(String.format("Что-то пошло не так при попытке создания папки для сохранения%n%s%n" +
                        "Возможно у вас нет прав для создания данного каталога или ошибка в имени пути.", args[INDEX_FOLDER_FOR_SAVE]));
            }
        }
    }
}
