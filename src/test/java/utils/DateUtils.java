package utils;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public final class DateUtils {

    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static LocalDate plusDays(int days) {
        return LocalDate.now().plusDays(days);
    }

    public static String monthRu(LocalDate date) {
        return date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
    }

}
