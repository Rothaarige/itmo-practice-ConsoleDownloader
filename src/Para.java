import java.util.ArrayList;

public class Para {
    private String link;
    private ArrayList<String> fileNames;

    public Para(String link, ArrayList<String> fileNames) {
        this.link = link;
        this.fileNames = fileNames;
    }

    public String getLink() {
        return link;
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }
}
