package attendance.controller;

import attendance.domain.attendance.Attendance;
import attendance.domain.crew.Crew;
import attendance.domain.crew.Crews;
import attendance.domain.menu.MainFunction;
import attendance.util.FileLoader;
import attendance.view.InputView;
import attendance.view.OutputView;
import camp.nextstep.edu.missionutils.DateTimes;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static attendance.domain.menu.MainFunction.*;
import static attendance.domain.rules.CampusTime.CLOSE_TIME;
import static attendance.domain.rules.CampusTime.OPEN_TIME;

public class AttendanceController {
    private final InputView inputView;
    private final OutputView outputView;
    private final Crews crews;

    public AttendanceController(){
        this.inputView = new InputView();
        this.outputView = new OutputView();
        this.crews = FileLoader.load("src/main/resources/attendance.csv");
    }

    public void run() {
        while (true){
            LocalDateTime now = DateTimes.now();
            outputView.printToday(now.toLocalDate());

            try {
                MainFunction function = MainFunction.from(inputView.readFunction());
                if(function.isQuit()){
                    break;
                }
                executeFunction(function, now);
            } catch (IllegalArgumentException e) {
                outputView.printError(e);
                throw e;
            }
        }
    }


    private void executeFunction(MainFunction function, LocalDateTime now) {
        if(function == CHECK_ATTENDANCE){
            checkAttendance(now);
            return;
        }
        if(function == EDIT_ATTENDANCE){
            editAttendance(now);
            return;
        }
        if(function == SHOW_RECORDS){
            showRecords(now);
            return;
        }
        if(function == CHECK_EXPULSION_DANGER){
            checkExpulsionDanger(now);
        }
    }

    // 기능 1. 출석하기
    private void checkAttendance(LocalDateTime now){
        validateOperatingHour(now.toLocalTime());
        validateWeekend(now.toLocalDate());

        String nickname = inputView.readNickname();
        Crew crew = crews.findByNickname(nickname);

        validateDuplicateAttendance(crew, now.toLocalDate());

        LocalTime schoolTime = parseTime(inputView.readSchoolTime());

        // 출석 추가
        Attendance newAttendance = new Attendance(now.toLocalDate(), schoolTime);
        crew.addAttendance(newAttendance);

        outputView.printAttendanceResult(now.toLocalDate(), crew, newAttendance.getStatus());

    }

    // 기능 2. 출석 수정
    private void editAttendance(LocalDateTime now){
        String nickname = inputView.readNicknameToEdit();
        int day = inputView.readDayToEdit();
        String timeInput = inputView.readTimeChangeTo();

        Crew crew = crews.findByNickname(nickname);
        LocalDate targetDate;

        try {
            targetDate = LocalDate.of(now.getYear(), now.getMonth(), day);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException(String.format("[ERROR] 올바르지 않은 날짜입니다 : %d", day));
        }

        validateEditDate(targetDate, now.toLocalDate());

        LocalTime newTime = parseTime(timeInput);

        // 업데이트 수행
        Attendance oldAttendance = crew.updateAttendance(targetDate, newTime);
        Attendance newAttendance = new Attendance(targetDate, newTime); // 임시 객체

        outputView.printEditComplete(
                targetDate,
                outputView.formatTime(oldAttendance.getTime()),
                outputView.formatTime(newTime),
                oldAttendance.getStatus(),
                newAttendance.getStatus()
        );
    }

    // 기능 3. 리포트
    private void showRecords(LocalDateTime now) {
        String nickname = inputView.readNickname();
        Crew crew = crews.findByNickname(nickname);
        outputView.printCrewAttendance(crew, now.toLocalDate());
    }

    // 기능 4. 제적 위험 리스트
    private void checkExpulsionDanger(LocalDateTime now){
        LocalDate today = now.toLocalDate();
        List<Crew> targets = crews.getCrews().stream()
                .filter(crew -> crew.isWarningTarget(today))
                .sorted(createExpulsionComparator(today))
                .toList();

        outputView.printExpulsionTargets(targets, today);
    }

    private Comparator<Crew> createExpulsionComparator(LocalDate today){
        return Comparator.comparing((Crew crew) -> getRiskPriority(crew, today), Comparator.reverseOrder()) // 1. 위험 단계 (높을수록 우선)
                .thenComparing(crew -> crew.getConvertedAbsenceCount(today), Comparator.reverseOrder())     // 2. 결석 횟수 (내림차순)
                .thenComparing(Crew::getNickname);
    }

    // 위험 단계별 우선순위 부여
    private int getRiskPriority(Crew crew, LocalDate today) {
        if (crew.isExpulsionTarget(today)) return 3; // 제적 대상자 (가장 높음)
        if (crew.isInterviewTarget(today)) return 2; // 면담 대상자
        if (crew.isWarningTarget(today)) return 1;   // 경고 대상자
        return 0;
    }

    // === Validation & Utils ===

    private void validateOperatingHour(LocalTime time){
        if(time.isBefore(OPEN_TIME.getTime()) || time.isAfter(CLOSE_TIME.getTime())){
            throw new IllegalArgumentException("[ERROR] 캠퍼스 운영 시간에만 출석이 가능합니다.");
        }
    }

    private void validateWeekend(LocalDate date){
        DayOfWeek day = date.getDayOfWeek();
        if(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY){
            String dayName = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.KOREAN));
            throw new IllegalArgumentException(
                    String.format("[ERROR] %d월 %d일 %s은 등교일이 아닙니다.",
                            date.getMonthValue(), date.getDayOfMonth(), dayName));
        }
    }

    private void validateDuplicateAttendance(Crew crew, LocalDate date){
        boolean alreadyAttended = crew.getAttendances().stream()
                .anyMatch(att -> att.getDate().equals(date) && att.getTime() != null);

        if (alreadyAttended) {
            throw new IllegalArgumentException("[ERROR] 이미 출석을 확인하였습니다. 필요한 경우 수정 기능을 이용해 주세요.");
        }
    }

    private void validateEditDate(LocalDate targetDate, LocalDate today){
        validateWeekend(targetDate);
        if(targetDate.isAfter(today)){
            throw new IllegalArgumentException("[ERROR] 아직 수정할 수 없습니다.");
        }
    }

    private LocalTime parseTime(String timeInput){
        return LocalTime.parse(timeInput, DateTimeFormatter.ofPattern("HH:mm"));
    }

}
