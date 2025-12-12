package attendance.view;

import attendance.domain.attendance.Attendance;
import attendance.domain.crew.Crew;
import attendance.domain.rules.AttendanceStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class OutputView {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM월 dd일 EEEE", Locale.KOREAN);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public void printToday(LocalDate date) {
        System.out.printf("오늘은 %s입니다. ", date.format(DATE_FORMATTER));
    }

    public void printAttendanceResult(LocalDate date, Crew crew, AttendanceStatus status) {
        // 가장 최근(오늘) 기록의 시간을 가져오기 위해 리스트의 마지막 요소 조회
        String timeString = crew.getAttendances().getLast().getTime().format(TIME_FORMATTER);

        System.out.println();
        System.out.printf("%s %s (%s)%n",
                date.format(DATE_FORMATTER),
                timeString,
                status.getDescription()
        );
        System.out.println();
    }

    public void printEditComplete(LocalDate oldDate, String oldTime, String newTime, AttendanceStatus oldStatus, AttendanceStatus newStatus) {
        System.out.println();
        System.out.printf("%s %s (%s) -> %s (%s) 수정 완료!%n",
                oldDate.format(DATE_FORMATTER),
                oldTime,
                oldStatus.getDescription(),
                newTime,
                newStatus.getDescription()
        );
        System.out.println();
    }

    public void printCrewAttendance(Crew crew) {
        System.out.println();
        System.out.printf("이번 달 %s의 출석 기록입니다.%n%n", crew.getNickname());

        for (Attendance att : crew.getAttendances()) {
            System.out.printf("%s %s (%s)%n",
                    att.getDate().format(DATE_FORMATTER),
                    att.getTime().format(TIME_FORMATTER),
                    att.getStatus().getDescription()
            );
        }
        System.out.println();

        printStats(crew);
    }

    private void printStats(Crew crew) {
        System.out.printf("출석: %d회%n", crew.getCount(AttendanceStatus.ATTENDANCE));
        System.out.printf("지각: %d회%n", crew.getCount(AttendanceStatus.LATE));
        System.out.printf("결석: %d회%n", crew.getCount(AttendanceStatus.ABSENT));
        System.out.println();

        printWarningMessage(crew);
    }

    private void printWarningMessage(Crew crew) {
        if (crew.isInterviewTarget()) {
            System.out.println("면담 대상자입니다.");
            System.out.println();
            return; // Indent depth 2 방지
        }
        if (crew.isWarningTarget()) {
            System.out.println("경고 대상자입니다.");
            System.out.println();
        }
    }

    public void printExpulsionTargets(List<Crew> targets) {
        System.out.println();
        System.out.println("제적 위험자 조회 결과");
        for (Crew crew : targets) {
            String status = getStatusString(crew);
            System.out.printf("- %s: 결석 %d회, 지각 %d회 (%s)%n",
                    crew.getNickname(),
                    crew.getCount(AttendanceStatus.ABSENT),
                    crew.getCount(AttendanceStatus.LATE),
                    status
            );
        }
        System.out.println();
    }

    // 상태 문자열 추출 로직 분리
    private String getStatusString(Crew crew) {
        if (crew.isExpulsionTarget()) return "제적";
        if (crew.isInterviewTarget()) return "면담";
        if (crew.isWarningTarget()) return "경고";
        return "";
    }

    public void printError(IllegalArgumentException e) {
        System.out.println(e.getMessage());
        System.out.println();
    }
}
