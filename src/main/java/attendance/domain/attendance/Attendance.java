package attendance.domain.attendance;

import attendance.domain.rules.AttendanceStatus;
import attendance.domain.rules.CampusTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Attendance {
    private final LocalDate date;
    private final LocalTime time;

    public Attendance(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public AttendanceStatus getStatus(){
        LocalTime startTime = CampusTime.getStartTime(date.getDayOfWeek());
        // 등교 시간보다 일찍 온 경우(음수) 0으로 처리하지 않으면 지각/결석 로직에 영향 없으므로 그대로 계산
        // 단, 로직상 '초과'분을 따지므로, 늦게 온 경우만 양수가 됨.
        long minutesDifference = ChronoUnit.MINUTES.between(startTime, time);

        return AttendanceStatus.of(minutesDifference);
    }

}
