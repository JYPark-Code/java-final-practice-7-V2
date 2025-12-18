package attendance.util;

import attendance.domain.crew.Crew;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static attendance.domain.rules.CampusTime.CLOSE_TIME;
import static attendance.domain.rules.CampusTime.OPEN_TIME;

public class AttendanceValidator {
    // === Validation & Utils ===

    public void validateOperatingHour(LocalTime time){
        if(time.isBefore(OPEN_TIME.getTime()) || time.isAfter(CLOSE_TIME.getTime())){
            throw new IllegalArgumentException("[ERROR] 캠퍼스 운영 시간에만 출석이 가능합니다.");
        }
    }

    public void validateWeekend(LocalDate date){
        DayOfWeek day = date.getDayOfWeek();
        if(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY){
            String dayName = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.KOREAN));
            throw new IllegalArgumentException(
                    String.format("[ERROR] %d월 %d일 %s은 등교일이 아닙니다.",
                            date.getMonthValue(), date.getDayOfMonth(), dayName));
        }
    }

    public void validateDuplicateAttendance(Crew crew, LocalDate date){
        boolean alreadyAttended = crew.getAttendances().stream()
                .anyMatch(att -> att.getDate().equals(date) && att.getTime() != null);

        if (alreadyAttended) {
            throw new IllegalArgumentException("[ERROR] 이미 출석을 확인하였습니다. 필요한 경우 수정 기능을 이용해 주세요.");
        }
    }

    public void validateEditDate(LocalDate targetDate, LocalDate today){
        validateWeekend(targetDate);
        if(targetDate.isAfter(today)){
            throw new IllegalArgumentException("[ERROR] 아직 수정할 수 없습니다.");
        }
    }
}
