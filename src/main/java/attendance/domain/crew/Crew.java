package attendance.domain.crew;

import attendance.domain.attendance.Attendance;
import attendance.domain.rules.AttendanceStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Crew {
    private final String nickname;
    private final List<Attendance> attendances;

    public Crew(String nickname) {
        this.nickname = nickname;
        this.attendances = new ArrayList<>();
    }

    public String getNickname() {
        return nickname;
    }

    public void addAttendance(Attendance attendance){
        attendances.add(attendance);
    }

    public List<Attendance> getAttendances() {
        return Collections.unmodifiableList(attendances);
    }

    public long getCount(AttendanceStatus status) {
        return attendances.stream()
                .filter(a -> a.getStatus() == status)
                .count();
    }

    public int getConvertedAbsenceCount() {
        long absentCount = getCount(AttendanceStatus.ABSENT);
        long lateCount = getCount(AttendanceStatus.LATE);
        return (int) (absentCount + lateCount / 3);
    }

    // 제적 위험자 정렬 + α 기능들

    // 제적 대상자
    public boolean isExpulsionTarget() {
        return getConvertedAbsenceCount() > 5;
    }

    public boolean isInterviewTarget() {
        return getConvertedAbsenceCount() >= 3;
    }

    public boolean isWarningTarget() {
        return getConvertedAbsenceCount() >= 2;
    }



}
