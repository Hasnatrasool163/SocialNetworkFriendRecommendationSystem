package org.example.dsassignment3_4.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SplashScreenController {

    @FXML
    private Group networkGroup;
    @FXML
    private Label titleLabel;
    @FXML
    private Button letsGoButton;

    private Random random = new Random();
    private Timeline patternSwitcher;


    private  final double CENTER_X = 410;
    private  final double CENTER_Y = 240;
    private  final double RADIUS = 180;

    public void initialize() {
        createNetworkAnimation();
        animateTitle();
        animateTitle2();
        addParticleEffects();
        rotateNetworkGroup();
        autoChangePattern();
    }

    public void switchToRegisterScene() {
        UtilityMethods.switchToScene(titleLabel, "Login");
    }

    private void animateTitle2() {
        Timeline colorAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(titleLabel.textFillProperty(), Color.DEEPSKYBLUE)),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(titleLabel.textFillProperty(), Color.WHITE)),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(titleLabel.textFillProperty(), Color.ORANGE))
        );
        colorAnimation.setCycleCount(Animation.INDEFINITE);
        colorAnimation.setAutoReverse(true);
        colorAnimation.play();
    }

    private void createNetworkAnimation() {
        networkGroup.getChildren().clear();
        int nodeCount = random.nextInt(4,14); // Random number of nodes (4-14)
        List<Circle> nodes = new ArrayList<>();

        for (int i = 0; i < nodeCount; i++) {
            double angle = 2 * Math.PI * i / nodeCount;
            double x = CENTER_X + RADIUS * Math.cos(angle);
            double y = CENTER_Y + RADIUS * Math.sin(angle);

            Circle node = new Circle(10, Color.WHITE);
            node.setEffect(new DropShadow(15, Color.LIGHTBLUE));
            node.setCenterX(x);
            node.setCenterY(y);
            nodes.add(node);
            networkGroup.getChildren().add(node);

            addNodeAnimation(node);
            addNodeInteractivity(node);
        }

        for (int i = 0; i < nodeCount; i++) {
            for (int j = i + 1; j < nodeCount; j++) {
                Line connection = new Line(
                        nodes.get(i).getCenterX(), nodes.get(i).getCenterY(),
                        nodes.get(j).getCenterX(), nodes.get(j).getCenterY());
                connection.setStroke(Color.LIGHTCYAN);
                connection.setStrokeWidth(1.5);
                connection.setOpacity(0.7);
                networkGroup.getChildren().add(connection);

                addConnectionEffect(connection);
            }
        }
    }

    private void addNodeAnimation(Circle node) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(3 + random.nextDouble()), node);
        transition.setByX(random.nextDouble() * 20 - 10);
        transition.setByY(random.nextDouble() * 20 - 10);
        transition.setAutoReverse(true);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();
    }

    private void addNodeInteractivity(Circle node) {
        node.setOnMouseEntered(event -> {
            node.setFill(Color.GOLD);
            Tooltip tooltip = new Tooltip("Node ID: " + node.hashCode());
            Tooltip.install(node, tooltip);
        });

        node.setOnMouseExited(event -> node.setFill(Color.WHITE));
    }

    private void addConnectionEffect(Line connection) {
        Glow glow = new Glow(0.3);
        connection.setEffect(glow);

        Timeline glowAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(glow.levelProperty(), 0.3)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(glow.levelProperty(), 1.0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(glow.levelProperty(), 0.3))
        );
        glowAnimation.setCycleCount(Animation.INDEFINITE);
        glowAnimation.setAutoReverse(true);
        glowAnimation.play();
        connection.setOnMouseEntered(event -> connection.setStroke(Color.ORANGE));
    }

    private void animateTitle() {
        FadeTransition fade = new FadeTransition(Duration.seconds(2), titleLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setCycleCount(1);
        fade.setOnFinished(event -> {
            ScaleTransition scale = new ScaleTransition(Duration.seconds(1), titleLabel);
            scale.setByX(0.1);
            scale.setByY(0.1);
            scale.setAutoReverse(true);
            scale.setCycleCount(Animation.INDEFINITE);
            scale.play();
        });
        fade.play();
    }

    private void addParticleEffects() {
        for (int i = 0; i < 30; i++) {
            Circle particle = new Circle(2, Color.WHITE);
            particle.setOpacity(0.5);
            particle.setLayoutX(random.nextDouble() * (800 - 20) + 10);
            particle.setLayoutY(random.nextDouble() * (600 - 20) + 10);
            networkGroup.getChildren().add(particle);

            TranslateTransition transition = new TranslateTransition(Duration.seconds(4 + random.nextDouble()), particle);
            transition.setByX(random.nextDouble() * 100 - 50);
            transition.setByY(random.nextDouble() * 100 - 50);
            transition.setCycleCount(Animation.INDEFINITE);
            transition.setAutoReverse(true);
            transition.play();
        }
    }

    private void rotateNetworkGroup() {
        RotateTransition rotate = new RotateTransition(Duration.seconds(90), networkGroup);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
//        rotate.setInterpolator(Interpolator.EASE_IN);
//        rotate.setAxis(new Point3D(CENTER_X+50,CENTER_Y-50, 0));
        rotate.setRate(0.5);
        rotate.play();

    }

    private void autoChangePattern() {
        patternSwitcher = new Timeline(new KeyFrame(Duration.seconds(5), event -> enableButton()));
        patternSwitcher.setCycleCount(Animation.INDEFINITE);
        patternSwitcher.play();
    }

    private void enableButton() {
        letsGoButton.setDisable(false);
    }
}
