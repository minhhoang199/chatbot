package com.example.chatwebproject.utils;

import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

@UtilityClass
@Slf4j
public class DateUtil {
    private final String ddMMyyyyHHmmss = "YYYY-MM-dd'T'HH:mm:ss";

    // Define the time zone (e.g., system default)
    private final ZoneId zoneId = ZoneId.systemDefault();

    public LocalDateTime instantToLocalDateTime(Instant i) {
        return LocalDateTime.ofInstant(i, zoneId);
    }

    public LocalDateTime convertStringToLocalDateTime(String strDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");
        try {
            return LocalDateTime.parse(strDate, formatter);
        } catch (DateTimeParseException e) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER,
                    new String[]{e.getMessage()});
        }
    }

    public LocalTime convertStringToLocalTime(String strDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            LocalTime localTimeUTC7 = LocalTime.parse(strDate, formatter);
            LocalDateTime localDateTime7 = LocalDateTime.of(LocalDate.now(), localTimeUTC7);
            ZonedDateTime zonedDateTimeUtc7 = ZonedDateTime.of(localDateTime7, ZoneOffset.ofHours(7));
            ZonedDateTime systemZonedDateTime = zonedDateTimeUtc7.withZoneSameInstant(zoneId);
            return systemZonedDateTime.toLocalTime();
        } catch (DateTimeParseException e) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER,
                    new String[]{e.getMessage()});
        }
    }

    public static Instant localDateTimeToInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(zoneId).toInstant();
    }

    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(ddMMyyyyHHmmss);
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            log.debug("DateUtil :: convertStringToDate : Invalid dateString, not format dd-MM-yyyy HH:mm:ss : {}", dateString);

            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER,
                    new String[]{e.getMessage()});
        }
    }

    public static LocalDateTime getFirstDateOfMonth(LocalDateTime dateTime) {
        return dateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime getLastDateOfMonth(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    public static String getCurrentDate(String format) {
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return currentDate.format(formatter);
    }

    public static LocalTime getDefaultLocalTime(int hours, int minutes) {
        ZonedDateTime zonedDateTimeUtcPlus7 = ZonedDateTime.of(LocalDate.now(), LocalTime.of(hours, minutes), ZoneOffset.ofHours(7));
        ZonedDateTime systemZonedDateTime = zonedDateTimeUtcPlus7.withZoneSameInstant(zoneId);
        return systemZonedDateTime.toLocalTime();
    }
}

