package com.pokergame;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;

/**
 * Raise view controller.
 *
 * @author Alessandro Maini
 * @version 2023.06.28
 */
public class PokerRaiseController {

    @FXML
    private Slider sliderRaise;
    private double baseValue;

    /**
     * Initialize the control class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {

    }

    /**
     * Draw the slider to select the amount to raise.
     *
     * @param minRaise the minimal raise allowed
     */
    public void setSlider(double minRaise) {
        baseValue = minRaise == 0 ? GameLogic.BLIND : minRaise;
        sliderRaise.setMax(baseValue * 10);
        sliderRaise.setMin(baseValue);
        sliderRaise.setMajorTickUnit(baseValue * 3);
        sliderRaise.setMinorTickCount(2);
        sliderRaise.setBlockIncrement(baseValue);
        sliderRaise.setSnapToTicks(true);
    }

    /**
     * Return the amount selected.
     *
     * @return the amount rounded as a multiple of the minimal raise
     */
    public long getRaise() {
        double valueDouble = sliderRaise.getValue();
        long mul = Math.round(valueDouble / baseValue);
        return mul * (long) baseValue;
    }
}
