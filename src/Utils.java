import java.time.Duration;
import java.time.LocalDateTime;

public class Utils {
    //return size in mega byte
    public static double getMB(long sizeBytes) {
        return Math.round((sizeBytes / 1024d / 1024) * 100) / 100d;
    }

    //return time in seconds
    public static long getDuration(LocalDateTime startTime) {
        return Duration.between(startTime, LocalDateTime.now()).toMillis() / 1000;
    }
}
