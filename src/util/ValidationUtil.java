package util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static LocalDate parseDate(String s) {
        try {
            return (s == null || s.trim().isEmpty()) ? null : LocalDate.parse(s.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static Float parseFloat(String s) {
        try {
            if (s == null || s.trim().isEmpty()) return null;
            float val = Float.parseFloat(s.trim().replace(",", ""));
            return val >= 0 ? val : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
