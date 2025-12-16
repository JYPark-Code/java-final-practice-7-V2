package attendance.domain.menu;

import java.util.Arrays;

public enum MainFunction {

    CHECK_ATTENDANCE("1"),
    EDIT_ATTENDANCE("2"),
    SHOW_RECORDS("3"),
    CHECK_EXPULSION_DANGER("4"),
    QUIT("Q");

    private final String command;

    MainFunction(String command){
        this.command = command;
    }

    public static MainFunction from(String command) {
        return Arrays.stream(values())
                .filter(function -> function.command.equalsIgnoreCase(command))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 잘못된 형식을 입력하셨습니다."));
    }

    public boolean isQuit(){
        return this == QUIT;
    }

}
