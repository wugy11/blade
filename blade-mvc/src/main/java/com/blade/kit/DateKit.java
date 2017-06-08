package com.blade.kit;

import com.blade.mvc.Const;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * date kit
 *
 * @author biezhi 2017/6/2
 */
public final class DateKit {

    private DateKit() {
        throw new IllegalStateException("DateKit shouldn't be constructed!");
    }

    /**
     * return current unix time
     */
    public static int nowUnix() {
        return (int) Instant.now().getEpochSecond();
    }

    /**
     * format unix time to string
     */
    public static String toString(long unixTime, String pattern) {
        return Instant.ofEpochSecond(unixTime).atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * format date to string
     */
    public static String toString(Date date, String pattern) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String toString(LocalDateTime date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String toString(LocalDateTime time) {
        return toString(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * format string time to unix time
     */
    public static int toUnix(String time, String pattern) {
        LocalDateTime formatted = LocalDateTime.parse(time, DateTimeFormatter.ofPattern(pattern));
        return (int) formatted.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * format string (yyyy-MM-dd HH:mm:ss) to unix time
     */
    public static int toUnix(String time) {
        return toUnix(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static int toUnix(Date date) {
        return (int) date.toInstant().getEpochSecond();
    }

    public static Date toDate(String time, String pattern) {
        LocalDate formatted = LocalDate.parse(time, DateTimeFormatter.ofPattern(pattern));
        return Date.from(Instant.from(formatted.atStartOfDay(ZoneId.systemDefault())));
    }

    public static Date toDate(long unixTime) {
        return Date.from(Instant.ofEpochSecond(unixTime));
    }

    public static String gmtDate() {
        ZoneId zone = ZoneId.of("GMT");
        return DateTimeFormatter.ofPattern(Const.HTTP_DATE_FORMAT, Locale.US).format(LocalDateTime.now().atZone(zone));
    }

    public static String gmtDate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.of("GMT");
        return DateTimeFormatter.ofPattern(Const.HTTP_DATE_FORMAT, Locale.US).format(localDateTime.atZone(zone));
    }

    public static String gmtDate(Date date) {
        ZoneId zone = ZoneId.of("GMT");
        return DateTimeFormatter.ofPattern(Const.HTTP_DATE_FORMAT, Locale.US)
                .format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).atZone(zone));

    }

}
