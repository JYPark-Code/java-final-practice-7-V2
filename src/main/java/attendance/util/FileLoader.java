package attendance.util;

import attendance.domain.attendance.Attendance;
import attendance.domain.crew.Crew;
import attendance.domain.crew.Crews;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileLoader {
    private static final int NICKNAME_INDEX = 0;
    private static final int DATETIME_INDEX = 1;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static Crews load(String filePath) throws FileNotFoundException {
        Map<String, Crew> crewMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // 헤더 처음 읽기

            while((line = br.readLine()) != null){
                processLine(line, crewMap);
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("[ERROR] 파일을 읽는 중 오류가 발생했습니다.");
        }
        return new Crews(new ArrayList<>(crewMap.values()));
    }

    private static void processLine(String line, Map<String, Crew> crewMap){
        String[] values = line.split(",");
        String nickname = values[NICKNAME_INDEX];
        String datetimeString = values[DATETIME_INDEX];

        Crew crew = crewMap.computeIfAbsent(nickname, Crew::new);

        // 날짜 파싱 + 출석기록 추가
        LocalDateTime localDateTime = LocalDateTime.parse(datetimeString, FORMATTER);
        Attendance attendance = new Attendance(localDateTime.toLocalDate(), localDateTime.toLocalTime());

        crew.addAttendance(attendance);
    }

}
