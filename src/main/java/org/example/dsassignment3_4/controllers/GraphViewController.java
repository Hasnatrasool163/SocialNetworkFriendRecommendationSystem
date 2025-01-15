package org.example.dsassignment3_4.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.dsassignment3_4.dao.GraphFriendSuggestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphViewController {

    @FXML
    private Canvas graphCanvas;

    @FXML
    private Button drawGraphButton;

    private GraphFriendSuggestion graphFriendSuggestion;

    public void initialize() {
        graphFriendSuggestion = new GraphFriendSuggestion();
        drawGraphButton.setOnAction(this::drawGraph);
    }

    private void drawGraph(ActionEvent event) {
        GraphicsContext gc = graphCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());

        Map<Integer, List<Integer>> graph = graphFriendSuggestion.getGraph();

        drawGraphNodes(gc, graph);
    }

    private void drawGraphNodes(GraphicsContext gc, Map<Integer, List<Integer>> graph) {
        double canvasWidth = graphCanvas.getWidth();
        double canvasHeight = graphCanvas.getHeight();
        double centerX = canvasWidth / 2;
        double centerY = canvasHeight / 2;
        double radius = Math.min(canvasWidth, canvasHeight) / 2.5;

        List<Integer> allNodes = graph.keySet().stream().sorted().toList();
        int totalNodes = allNodes.size();
        double angleIncrement = 360.0 / totalNodes;

        Map<Integer, double[]> nodePositions = new HashMap<>();

        for (int i = 0; i < totalNodes; i++) {
            double angle = Math.toRadians(i * angleIncrement);
            double nodeX = centerX + radius * Math.cos(angle);
            double nodeY = centerY + radius * Math.sin(angle);

            nodePositions.put(allNodes.get(i), new double[]{nodeX, nodeY});
        }

        drawConnections(gc, graph, nodePositions);

        for (int nodeId : allNodes) {
            double[] position = nodePositions.get(nodeId);
            drawNode(gc, nodeId, position[0], position[1]);
        }
    }

    private void drawNode(GraphicsContext gc, int nodeId, double x, double y) {
        double nodeRadius = 20;
        gc.setFill(Color.ORANGE);
        gc.fillOval(x - nodeRadius, y - nodeRadius, nodeRadius * 2, nodeRadius * 2);

//        Tooltip = new Tooltip("Node ID: " + nodeId);
//        Tooltip.install(gc.getCanvas(), tooltip);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font(14));
        String label = String.valueOf(nodeId);
        gc.fillText(label, x - gc.getFont().getSize() / 2.5, y + gc.getFont().getSize() / 3);
    }

    private void drawConnections(GraphicsContext gc, Map<Integer, List<Integer>> graph, Map<Integer, double[]> nodePositions) {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
            int node = entry.getKey();
            double[] fromPosition = nodePositions.get(node);

            for (int neighbor : entry.getValue()) {
                double[] toPosition = nodePositions.get(neighbor);

                if (node < neighbor) {
                    gc.strokeLine(fromPosition[0], fromPosition[1], toPosition[0], toPosition[1]);
                }
            }
        }
    }
}
