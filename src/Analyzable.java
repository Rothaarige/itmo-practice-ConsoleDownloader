public interface Analyzable {
    void onStart(String fileName);

    void onFinish(String fileName, double size, long time);

    void onErrors();
}
