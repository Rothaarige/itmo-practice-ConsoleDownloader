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
        System.out.println("Загружается файл: " + fileName); // + " потоком " + Thread.currentThread().getName());
    }

    synchronized public void onStartCopy(String fileNameFrom, String fileNameTo) {
        System.out.println("Файл: " + fileNameFrom + " копируется в: " + fileNameTo);
        ;
    }

    @Override
    synchronized public void onFinish(String fileName, double size, long time) {
        System.out.println("Файл: " + fileName + " загружен: " +
                size + " МБ за " +
                time + " сек");
        countDownloadFiles++;
        this.size = size;
    }

    synchronized public void onFinishCopy(String fileName, double size, long time) {
        System.out.println("Файл: " + fileName + " скопирован: " +
                size + " МБ за " +
                time + " сек");
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
        System.out.println("Загружено файлов: " + countDownloadFiles + System.lineSeparator() +
                "Сохранено файлов: " + (countDownloadFiles + countCopyFiles) + System.lineSeparator() +
                "Объем скаченных файлов: " + size);
        if (countErrorsDownloadFiles + countErrorsCopyFiles > 0) {
            System.out.println("Были ошибки при загрузке и сохранении файлов в количестве: " +
                    (countErrorsDownloadFiles + countErrorsCopyFiles) + System.lineSeparator() +
                    "Данные выше могут быть не верны.");
        }
        System.out.println("Время работы программы: " + (time / 60) + " мин " +
                +(time % 60) + " сек");
    }
}
