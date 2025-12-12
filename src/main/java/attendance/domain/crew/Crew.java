package attendance.domain.crew;

import attendance.domain.attendance.Attendance;
import attendance.domain.rules.AttendanceStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public Attendance updateAttendance(LocalDate date, LocalTime newTime){
        Attendance oldAttendance = findAttendanceByDate(date);

        // 기존 기록 삭제
        if (oldAttendance.getTime() != null) {
            attendances.remove(oldAttendance);
        }

        // 새 기록 추가
        Attendance newAttendance = new Attendance(date, newTime);
        attendances.add(newAttendance);

        // 날짜 순 정렬
        attendances.sort(Comparator.comparing(Attendance::getDate));

        return oldAttendance;
    }

    private Attendance findAttendanceByDate(LocalDate date) {
        return attendances.stream()
                .filter(a -> a.getDate().equals(date))
                .findFirst()
                .orElse(new Attendance(date, null)); // 기록이 없으면 출석 시간이 null로 저장
    }

    // 3번, 4번 기능을 위해 만드는 메소드 : 전날까지 히스토리 찾기
    public List<Attendance> getHistoryUpToYesterday(LocalDate today) {
        List<Attendance> history = new ArrayList<>();
        LocalDate startDate = today.withDayOfMonth(1);
        LocalDate yesterday = today.minusDays(1);

        // 1일부터 어제까지 반복
        for(LocalDate date = startDate; !date.isAfter(yesterday); date = date.plusDays(1)){
            if (isWeekend(date)) continue;

            Attendance attendance = findAttendanceByDate(date); // 기록이 없으면 null 시간 객체 반환됨
            history.add(attendance);
        }

        return history;
    }

    private boolean isWeekend(LocalDate date){
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }


    public long getCount(AttendanceStatus status, LocalDate today) {
        List<Attendance> history = getHistoryUpToYesterday(today);
        return history.stream()
                .filter(a -> a.getStatus() == status)
                .count();
    }

    public int getConvertedAbsenceCount(LocalDate today) {
        long absentCount = getCount(AttendanceStatus.ABSENT, today);
        long lateCount = getCount(AttendanceStatus.LATE, today);
        return (int) (absentCount + lateCount / 3);
    }

    // 제적 위험자 정렬 + α 기능들

    // 제적 대상자
    public boolean isExpulsionTarget(LocalDate today) {
        return getConvertedAbsenceCount(today) > 5;
    }

    public boolean isInterviewTarget(LocalDate today) {
        return getConvertedAbsenceCount(today) >= 3;
    }

    public boolean isWarningTarget(LocalDate today) {
        return getConvertedAbsenceCount(today) >= 2;
    }



}
