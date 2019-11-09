import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {

        int numbersTread = Integer.parseInt("2");
        String links = "links.txt";
        String folderForSave = "files";

        if (!Files.isDirectory(Paths.get(folderForSave))){
            Files.createDirectories(Paths.get(folderForSave));
        }

//        int numbersTread = Integer.getInteger(args[0]);
//        String links =args[1];
//        String folderForSave =args[2];

        Map<String, ArrayList<String>> sourse = Parser.parse(links);
        int cnt = 0;
        for (Map.Entry<String, ArrayList<String>> entry : sourse.entrySet()) {
            Download.download(entry.getKey(), entry.getValue(), folderForSave);
        }

    }


}
