package com.pokergame;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;

public class PokerRaiseController {

    @FXML
    private Slider sliderRaise;
    public double baseValue;

    @FXML
    public void initialize() {

    }

    public void setSlider(double minRaise) {
        baseValue = minRaise == 0 ? PokerGameController.BLIND : minRaise;
        sliderRaise.setMax(baseValue * 10);
        sliderRaise.setMin(baseValue);
        sliderRaise.setMajorTickUnit(baseValue * 2);
        sliderRaise.setMinorTickCount(1);
        sliderRaise.setBlockIncrement(baseValue);
        sliderRaise.setSnapToTicks(true);
    }

    public long getRaise() {
        double valueDouble = sliderRaise.getValue();
        long mul = Math.round(valueDouble / baseValue);
        return mul * (long) baseValue;
    }

}
