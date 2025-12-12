package attendance.controller;

import attendance.domain.crew.Crews;
import attendance.util.FileLoader;
import attendance.view.InputView;
import attendance.view.OutputView;

import java.io.FileNotFoundException;

public class AttendanceController {
    private final InputView inputView;
    private final OutputView outputView;
    private final Crews crews;

    public AttendanceController(){
        this.inputView = new InputView();
        this.outputView = new OutputView();
        this.crews = FileLoader.load("src/main/resources/attendance.csv");
    }



}
