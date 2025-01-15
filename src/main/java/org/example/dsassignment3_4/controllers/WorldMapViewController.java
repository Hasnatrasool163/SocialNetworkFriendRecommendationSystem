package org.example.dsassignment3_4.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import org.controlsfx.control.WorldMapView;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.net.URL;
import java.util.ResourceBundle;

public class WorldMapViewController implements Initializable {

    @FXML
    private WorldMapView worldMapView;

    private  String selectedLocationName;

    public static WorldMapViewController instance;

    public WorldMapViewController() {
        if (instance == null) {
            instance = this;
        };
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        worldMapView.setCountries(FXCollections.observableArrayList(WorldMapView.Country.PK));
//        worldMapView.setZoomFactor(1.9);

        WorldMapView.Location punjab = new WorldMapView.Location("Punjab", 389, 72);
        WorldMapView.Location sindh = new WorldMapView.Location("Sindh", 386, 69);
        WorldMapView.Location kp = new WorldMapView.Location("KPK", 392, 70);
        WorldMapView.Location balochistan = new WorldMapView.Location("Balochistan", 387, 64.5);
        WorldMapView.Location gilgitBaltistan = new WorldMapView.Location("GB", 395, 73);
        WorldMapView.Location azadKashmir = new WorldMapView.Location("Kashmir", 394, 75);

        ObservableList<WorldMapView.Location> locations = FXCollections.observableArrayList(
                punjab, sindh, kp, balochistan, gilgitBaltistan, azadKashmir);

        worldMapView.setShowLocations(true);
        worldMapView.setLocations(locations);
        worldMapView.setTooltip(new Tooltip(locations.get(0).getName()));

        worldMapView.setOnMouseClicked(event -> {
            ObservableList<WorldMapView.Location> selectedLocations = worldMapView.getSelectedLocations();
            ObservableList<WorldMapView.Country> countries = worldMapView.getSelectedCountries();
            if (!selectedLocations.isEmpty()) {
                selectedLocationName = selectedLocations.get(0).getName();
                UtilityMethods.showPopup("You selected: " + selectedLocationName);
                Stage stage = (Stage) worldMapView.getScene().getWindow();
                stage.close();
            }
            if(!countries.isEmpty()) {
                UtilityMethods.showPopup("You selected: " + countries.get(0).getLocale().getDisplayName());
            }
            });
    }

    public  String getSelectedLocation() {
        return selectedLocationName;
    }
}
