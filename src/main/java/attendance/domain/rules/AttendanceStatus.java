package attendance.domain.rules;

// 출석 상태
public enum AttendanceStatus {
    ABSENT("결석"),
    LATE("지각"),
    ATTENDANCE("출석");

    private final String description;

    AttendanceStatus(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static AttendanceStatus of(long minutesDifference) {
        if (CampusTime.isAbsent(minutesDifference)) {
            return ABSENT;
        }
        if (CampusTime.isLate(minutesDifference)){
            return LATE;
        }
        return ATTENDANCE;
    }
}
