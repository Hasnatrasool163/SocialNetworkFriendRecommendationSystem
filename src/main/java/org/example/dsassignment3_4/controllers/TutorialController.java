package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.controlsfx.control.PopOver;

public class TutorialController {

    @FXML
    private Button btn1;

    @FXML
    private Button btn2;

    @FXML
    private Button btn3;

    @FXML
    private Button nextButton;

    private PopOver popover;
    private int currentStep = 0;

    public void initialize() {
        initializePopover();
        nextButton.setOnAction(event -> goToNextStep());
    }

    private void initializePopover() {
        popover = new PopOver();
        popover.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        setPopoverForButton(btn1, "This is Button 1. It allows you to perform action A.");

        setPopoverForButton(btn2, "This is Button 2. It helps you with action B.");

        setPopoverForButton(btn3, "This is Button 3. It lets you perform action C.");
        
        popover.hide();
    }

    private void setPopoverForButton(Button button, String message) {
        button.setOnMouseEntered(event -> {
            if (popover.isShowing()) {
                popover.hide();
            }
            popover.setContentNode(new Label(message));
            popover.show(button);
        });
        
        button.setOnMouseExited(event -> popover.hide());
    }

    private void goToNextStep() {
        switch (currentStep) {
            case 0:
                popover.show(btn1);
                break;
            case 1:
                btn2.fire();
                btn2.requestFocus();
                popover.show(btn2);
                break;
            case 2:
                btn2.fire();
                btn3.requestFocus();
                popover.show(btn3);
                break;
            default: nextButton.setDisable(true);
                popover.hide();
                break;
        }
        currentStep++;
    }
}
