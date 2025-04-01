package com.transport;

import com.transport.model.location.Location;
import com.transport.model.passenger.*;
import com.transport.model.route.Route;
import com.transport.model.route.RouteSegment;
import com.transport.model.stop.Stop;
import com.transport.model.vehicle.Bus;
import com.transport.model.vehicle.Taxi;
import com.transport.model.vehicle.Tram;
import com.transport.model.vehicle.Vehicle;
import com.transport.service.DataLoader;
import com.transport.service.RouteCalculator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class TransportApplication extends Application {
    private DataLoader dataLoader;
    private RouteCalculator routeCalculator;
    private ComboBox<String> passengerTypeCombo;
    private ComboBox<String> paymentMethodCombo;
    private TextField startLatField;
    private TextField startLonField;
    private TextField endLatField;
    private TextField endLonField;
    private TextArea resultArea;

    @Override
    public void start(Stage primaryStage) {
        try {
            dataLoader = new DataLoader("veriseti.json");
            dataLoader.loadData();
            routeCalculator = new RouteCalculator(dataLoader.getStops());
        } catch (IOException e) {
            showError("Veri yükleme hatası: " + e.getMessage());
            return;
        }

        primaryStage.setTitle("İzmit Ulaşım Sistemi");

        // Ana düzen
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Yolcu tipi ve ödeme yöntemi seçimi
        HBox selectionBox = new HBox(20);
        selectionBox.setAlignment(Pos.CENTER);

        // Yolcu tipi seçimi
        HBox passengerBox = new HBox(10);
        passengerBox.setAlignment(Pos.CENTER);
        Label passengerLabel = new Label("Yolcu Tipi:");
        passengerTypeCombo = new ComboBox<>();
        passengerTypeCombo.getItems().addAll("Normal Yolcu", "Öğrenci", "Yaşlı");
        passengerTypeCombo.setValue("Normal Yolcu");
        passengerTypeCombo.setPrefWidth(200);
        passengerBox.getChildren().addAll(passengerLabel, passengerTypeCombo);

        // Ödeme yöntemi seçimi
        HBox paymentBox = new HBox(10);
        paymentBox.setAlignment(Pos.CENTER);
        Label paymentLabel = new Label("Ödeme Yöntemi:");
        paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll("Kentkart", "Nakit");
        paymentMethodCombo.setValue("Kentkart");
        paymentMethodCombo.setPrefWidth(200);
        paymentBox.getChildren().addAll(paymentLabel, paymentMethodCombo);

        selectionBox.getChildren().addAll(passengerBox, paymentBox);

        // Koordinat girişi
        GridPane coordGrid = new GridPane();
        coordGrid.setHgap(10);
        coordGrid.setVgap(10);
        coordGrid.setAlignment(Pos.CENTER);
        coordGrid.setPadding(new Insets(20, 0, 20, 0));

        // Başlangıç koordinatları
        coordGrid.add(new Label("Başlangıç:"), 0, 0);
        coordGrid.add(new Label("Enlem:"), 1, 0);
        startLatField = new TextField();
        startLatField.setPrefWidth(120);
        coordGrid.add(startLatField, 2, 0);
        coordGrid.add(new Label("Boylam:"), 3, 0);
        startLonField = new TextField();
        startLonField.setPrefWidth(120);
        coordGrid.add(startLonField, 4, 0);

        // Varış koordinatları
        coordGrid.add(new Label("Varış:"), 0, 1);
        coordGrid.add(new Label("Enlem:"), 1, 1);
        endLatField = new TextField();
        endLatField.setPrefWidth(120);
        coordGrid.add(endLatField, 2, 1);
        coordGrid.add(new Label("Boylam:"), 3, 1);
        endLonField = new TextField();
        endLonField.setPrefWidth(120);
        coordGrid.add(endLonField, 4, 1);

        // Hesapla butonu
        Button calculateButton = new Button("Rota Hesapla");
        calculateButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 20;");
        calculateButton.setOnAction(e -> calculateRoute());

        // Sonuç alanı
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(20);
        resultArea.setPrefColumnCount(50);
        resultArea.setStyle("-fx-font-family: 'Monospace'; -fx-font-size: 14px;");
        resultArea.setWrapText(true);

        // Tüm bileşenleri ana düzene ekle
        root.getChildren().addAll(selectionBox, coordGrid, calculateButton, resultArea);

        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Örnek koordinatları göster
        showExampleCoordinates();
    }

    private void showExampleCoordinates() {
        startLatField.setPromptText("Örn: 40.7654");
        startLonField.setPromptText("Örn: 29.9167");
        endLatField.setPromptText("Örn: 40.8245");
        endLonField.setPromptText("Örn: 29.9167");
    }

    private void calculateRoute() {
        try {
            double startLat = Double.parseDouble(startLatField.getText());
            double startLon = Double.parseDouble(startLonField.getText());
            double endLat = Double.parseDouble(endLatField.getText());
            double endLon = Double.parseDouble(endLonField.getText());

            // Yolcu tipini belirle
            Passenger passenger = switch (passengerTypeCombo.getValue()) {
                case "Öğrenci" -> new StudentPassenger();
                case "Yaşlı" -> new ElderlyPassenger();
                default -> new RegularPassenger();
            };

            // Ödeme yöntemini belirle
            boolean isKentkart = paymentMethodCombo.getValue().equals("Kentkart");
            passenger.setPaymentMethod(isKentkart ? "Kentkart" : "Nakit");

            // Ana rotayı hesapla
            Route mainRoute = routeCalculator.calculateRoute(
                new Location(startLat, startLon),
                new Location(endLat, endLon),
                passenger
            );

            // Alternatif rotaları hesapla
            List<Route> alternativeRoutes = routeCalculator.calculateAlternativeRoutes(
                new Location(startLat, startLon),
                new Location(endLat, endLon),
                passenger
            );

            // Sonuçları göster
            StringBuilder result = new StringBuilder();
            result.append(mainRoute.toString());
            
            // Alternatif rotaları ekle
            result.append("\n🛤 Alternatif Rotalar:\n");
            result.append("──────────────────────\n");
            
            for (Route route : alternativeRoutes) {
                String routeType = "";
                String description = "";
                
                // Rota tipini belirle
                if (route.getSegments().stream().allMatch(s -> s.getVehicle() instanceof Taxi)) {
                    routeType = "🚖 Sadece Taksi";
                    description = "(Daha hızlı, ancak maliyetli)";
                } else if (route.getSegments().stream().allMatch(s -> s.getVehicle() instanceof Bus)) {
                    routeType = "🚍 Sadece Otobüs";
                    description = "(Daha uygun maliyetli, ancak daha uzun sürebilir)";
                } else if (route.getSegments().stream().anyMatch(s -> s.getVehicle() instanceof Tram)) {
                    routeType = "🚋 Tramvay Öncelikli";
                    description = "(Rahat ve dengeli bir ulaşım seçeneği)";
                } else {
                    routeType = "🛑 En Az Aktarmalı Rota";
                    description = "(Daha az durak, daha az bekleme süresi)";
                }
                
                result.append("🔹 ").append(routeType).append(" ").append(description).append("\n");
                result.append("   • Mesafe: ").append(String.format("%.2f", route.getTotalDistance())).append(" km\n");
                result.append("   • Süre: ").append(String.format("%.0f", route.getTotalDuration())).append(" dakika\n");
                result.append("   • Ücret: ").append(String.format("%.2f", route.getTotalFare())).append(" TL\n\n");
            }

            resultArea.setText(result.toString());

        } catch (NumberFormatException e) {
            showError("Lütfen geçerli koordinat değerleri girin.");
        } catch (Exception e) {
            showError("Rota hesaplama hatası: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 