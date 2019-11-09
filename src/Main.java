import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class Main {
    private static final String ERROR_MESSAGE = "Данные должны быть вида:\n" +
            "-------------------------\n" +
            "N PathFile FolderForSave\n" +
            "-------------------------\n" +
            "где N - количество одновременно качающих потоков\n" +
            "PathFile - путь к файлу со списком ссылок\n" +
            "FolderForSave - имя папки, куда складывать скаченные файлы";
    private static String links;
    private static String folderForSave;
    private static int numbersTread;

    public static void main(String[] args) {
        try {
            checkParameters(args);

            Map<String, ArrayList<String>> sourse = Parser.parse(links);

            int cnt = 0;
            for (Map.Entry<String, ArrayList<String>> entry : sourse.entrySet()) {
                Download.download(entry.getKey(), entry.getValue(), folderForSave);
            }

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void checkParameters(String[] args) {
        //Проверка входных данных
//        if (args.length != 3){
//            throw new RuntimeException("Введены некорректные данные.\n"+ ERROR_MESSAGE);
//        }
        //Проверка первого аргумента - количества потоков
        try {
            numbersTread = Integer.parseInt("2");
//            int numbersTread = Integer.getInteger(args[0]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Неверно введено количесвто потоков.\n" + ERROR_MESSAGE);
        }
        //Проверка второго аргумента - файла с данными
        links = "links.txt";
//        String links =args[1];
        if (!Files.exists(Paths.get(links))) {
            throw new RuntimeException("Файл: " + links + " отсутствует.\n" + ERROR_MESSAGE);
        }

        //Проверка третьего аргумента - папки для копирования
        folderForSave = "files";
//        String folderForSave =args[2];
        if (!Files.isDirectory(Paths.get(folderForSave))) {
            try {
                Files.createDirectories(Paths.get(folderForSave));
            } catch (IOException e) {
                throw new RuntimeException("Что-то пошло не так при попытке создания папки для сохранения\n" +
                        folderForSave + "\n" +
                        "Возможно у вас нет прав для создания данного каталога или ошибка в имени пути.");
            }
        }
    }

}
