package it.unipi.healthhub.util;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

public class DateUtil {
    public static LocalDate getFirstDayOfWeek(int week, int year) {
        // Get the first day of the first week of the year
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
        // Get the first day of the requested week
        LocalDate firstDayOfWeek = firstDayOfYear
                .with(WeekFields.of(Locale.getDefault()).weekOfYear(), week)
                .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);

        //DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return firstDayOfWeek;
    }

    public static boolean isSameWeek(LocalDate date1, LocalDate date2) {
        java.time.temporal.WeekFields weekFields = java.time.temporal.WeekFields.ISO;
        return date1.get(weekFields.weekOfWeekBasedYear()) == date2.get(weekFields.weekOfWeekBasedYear())
                && date1.getYear() == date2.getYear();
    }

    public static List<LocalDate> getNext4Mondays(LocalDate date) {
        LocalDate start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        start = start.plusWeeks(1);
        LocalDate end = start.plusWeeks(3);
        return start.datesUntil(end.plusDays(1))
                .filter(d -> d.getDayOfWeek() == DayOfWeek.MONDAY)
                .toList();
    }

    public static LocalDate convertToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
