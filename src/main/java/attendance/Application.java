package attendance;

import attendance.controller.AttendanceController;
import attendance.util.AttendanceValidator;
import attendance.repository.CrewRepository;
import attendance.repository.FileCrewRepository;
import attendance.view.InputView;
import attendance.view.OutputView;

public class Application {
    public static void main(String[] args) {
        // 1. 의존성 객체 생성 (부품 준비)
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        AttendanceValidator validator = new AttendanceValidator();

        // 데이터 저장소 결정 (CSV 파일을 사용하는 Repository 생성)
        CrewRepository crewRepository = new FileCrewRepository("src/main/resources/attendance.csv");

        // 2. 컨트롤러 생성 및 주입 (조립)
        AttendanceController controller = new AttendanceController(
                inputView,
                outputView,
                validator,
                crewRepository
        );

        // 3. 실행
        controller.run();
    }
}
