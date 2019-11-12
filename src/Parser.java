import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Parser {

    public ConcurrentLinkedQueue<Pair<String, ArrayList<String>>> parse(String nameFile) {
        Map<String, ArrayList<String>> fileList = new HashMap<>();
        ConcurrentLinkedQueue<Pair<String, ArrayList<String>>> filesForSave = new ConcurrentLinkedQueue<>();

        try (FileReader fr = new FileReader(nameFile);
             BufferedReader bf = new BufferedReader(fr)) {
            String data;
            while ((data = bf.readLine()) != null) {
                String[] tempStr = data.split(" ");
                if (fileList.get(tempStr[0]) == null) {
                    fileList.put(tempStr[0], new ArrayList<>());
                }
                fileList.get(tempStr[0]).add(tempStr[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException("Возникли проблемы при чтении файла: " + nameFile);
        }
        for (Map.Entry<String, ArrayList<String>> entry : fileList.entrySet()) {
            filesForSave.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        return filesForSave;
    }
}
