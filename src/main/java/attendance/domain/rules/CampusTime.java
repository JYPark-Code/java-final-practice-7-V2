package attendance.domain.rules;

import java.time.DayOfWeek;
import java.time.LocalTime;

// 등교 시간 및 운영 규칙
public enum CampusTime {
    MONDAY_START_TIME(13, 0),
    NORMAL_START_TIME(10, 0),
    OPEN_TIME(8, 0),
    CLOSE_TIME(23, 0);

    private static final int LATE_THRESHOLD_MINUTES = 5;
    private static final int ABSENT_THRESHOLD_MINUTES = 30;

    private final LocalTime time;

    CampusTime(int hour, int minute){
        this.time = LocalTime.of(hour, minute);
    }

    public LocalTime getTime() {
        return time;
    }

    public static LocalTime getStartTime(DayOfWeek dayOfWeek) {
        if (dayOfWeek == DayOfWeek.MONDAY) {
            return MONDAY_START_TIME.time;
        }
        return NORMAL_START_TIME.time;
    }

    public static boolean isLate(long minutesDifference) {
        return minutesDifference > LATE_THRESHOLD_MINUTES;
    }

    public static boolean isAbsent(long minuteDifference) {
        return minuteDifference > ABSENT_THRESHOLD_MINUTES;
    }


}
