package attendance.view;

import camp.nextstep.edu.missionutils.Console;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InputView {

    public String readFunction() {
        System.out.println("기능을 선택해 주세요,");
        System.out.println("1. 출석 확인");
        System.out.println("2. 출석 수정");
        System.out.println("3. 크루 별 출석 기록 확인");
        System.out.println("4. 제적 위험자 확인");
        System.out.println("Q. 종료");

        String input = Console.readLine();
        validateNotEmpty(input);
        return input;
    }

    public String readNickname(){
        System.out.println("닉네임을 입력해 주세요.");
        String input = Console.readLine();
        validateNotEmpty(input);
        return input;
    }

    public String readSchoolTime() {
        System.out.println("등교 시간을 입력해 주세요.");
        String input = Console.readLine();
        validateTimeFormat(input);
        return input;
    }

    public String readNicknameToEdit() {
        System.out.println("출석을 수정하려는 크루의 닉네임을 입력해 주세요.");
        String input = Console.readLine();
        validateNotEmpty(input);
        return input;
    }

    public int readDayToEdit() {
        System.out.println("수정하려는 날짜(일)를 입력해 주세요.");
        String input = Console.readLine();
        validateNumber(input);
        return Integer.parseInt(input);
    }

    public String readTimeChangeTo() {
        System.out.println("언제로 변경하겠습니까?");
        String input = Console.readLine();
        validateTimeFormat(input);
        return input;
    }

    private void validateNotEmpty(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("[ERROR] 잘못된 형식을 입력하였습니다.");
        }
    }

    private void validateTimeFormat(String input) {
        validateNotEmpty(input);
        try {
            // HH:mm 형식으로 파싱되는지 확인 (예: 25:00, 09:60 등도 여기서 걸러짐)
            LocalTime.parse(input, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("[ERROR] 잘못된 형식을 입력하였습니다.");
        }
    }

    private void validateNumber(String input) {
        validateNotEmpty(input);
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] 잘못된 형식을 입력하였습니다.");
        }
    }

}
