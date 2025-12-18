package attendance.controller;

import attendance.domain.attendance.Attendance;
import attendance.domain.crew.Crew;
import attendance.domain.crew.Crews;
import attendance.domain.menu.MainFunction;
import attendance.repository.CrewRepository;
import attendance.util.AttendanceValidator;
import attendance.view.InputView;
import attendance.view.OutputView;
import camp.nextstep.edu.missionutils.DateTimes;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static attendance.domain.menu.MainFunction.*;


public class AttendanceController {
    private final InputView inputView;
    private final OutputView outputView;
    private final Crews crews;
    private final AttendanceValidator validator;

    public AttendanceController(
            InputView inputView,
            OutputView outputView,
            AttendanceValidator validator,
            CrewRepository crewRepository
    ){
        this.inputView =  inputView;
        this.outputView = outputView;
        this.crews = crewRepository.load();
        this.validator = validator;
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
        validator.validateOperatingHour(now.toLocalTime());
        validator.validateWeekend(now.toLocalDate());

        String nickname = inputView.readNickname();
        Crew crew = crews.findByNickname(nickname);

        validator.validateDuplicateAttendance(crew, now.toLocalDate());

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

        validator.validateEditDate(targetDate, now.toLocalDate());

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
                .sorted(Crew.createExpulsionComparator(today))
                .toList();

        outputView.printExpulsionTargets(targets, today);
    }


    private LocalTime parseTime(String timeInput){
        return LocalTime.parse(timeInput, DateTimeFormatter.ofPattern("HH:mm"));
    }

}
