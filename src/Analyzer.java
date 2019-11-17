public class Analyzer implements Analyzable {
    private volatile int countErrorsDownloadFiles = 0;
    private volatile int countErrorsCopyFiles = 0;
    private volatile int countDownloadFiles = 0;
    private volatile int countCopyFiles = 0;
    private volatile double size = 0;

    public Analyzer() {
    }

    @Override
    synchronized public void onStart(String fileName) {
        System.out.printf("Загружается файл: %s %n", fileName); // + " потоком " + Thread.currentThread().getName());
    }

    synchronized public void onStartCopy(String fileNameFrom, String fileNameTo) {
        System.out.printf("Файл: %s  копируется в: %s %n", fileNameFrom, fileNameTo);
    }

    @Override
    synchronized public void onFinish(String fileName, double size, long time) {
        System.out.printf("Файл: %s загружен: %.2f МБ за %d сек %n", fileName, size, time);
        countDownloadFiles++;
        this.size += size;
    }

    synchronized public void onFinishCopy(String fileName, double size, long time) {
        System.out.printf("Файл: %s  скопирован: %.2f МБ за %d сек %n", fileName, size, time);
        countCopyFiles++;
    }

    @Override
    synchronized public void onErrors() {
        countErrorsDownloadFiles++;
    }

    synchronized public void onErrorsCopy() {
        countErrorsCopyFiles++;
    }

    public void getResult(long time) {
        System.out.printf("Загружено файлов: %d %nСохранено файлов: %d %nОбъем скаченных файлов: %.2f MB %n",
                countDownloadFiles, (countDownloadFiles + countCopyFiles), size);
        if (countErrorsDownloadFiles + countErrorsCopyFiles > 0) {
            System.out.printf("Были ошибки при загрузке и сохранении файлов в количестве: %d %nДанные выше могут быть не верны. %n",
                    (countErrorsDownloadFiles + countErrorsCopyFiles));
        }
        System.out.printf("Время работы программы: %d мин %d сек %n", (time / 60), (time % 60));
        System.out.printf("Средняя скорость загрузки: %.2f Mбит/сек%n", size * 8 / time);
    }
}
