package org.example.dsassignment3_4.extras;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class PostCell extends ListCell<VBox> {
        @Override
        protected void updateItem(VBox item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(item);
                if (isSelected()) {
                    item.setStyle("-fx-background-color: skyblue; -fx-text-fill: white;");
                    setTextFill(javafx.scene.paint.Color.WHITE);
                } else {
                    item.setStyle("-fx-background-color: white; -fx-text-fill: black;");
                    setTextFill(javafx.scene.paint.Color.BLACK);
                }
            }
        }
}
