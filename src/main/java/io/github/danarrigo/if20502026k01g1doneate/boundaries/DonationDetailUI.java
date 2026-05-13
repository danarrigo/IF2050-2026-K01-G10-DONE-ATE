package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;

public class DonationDetailUI extends UI {

    private Donation donation;
    private HBox topBar;

    private final String DARK_GREEN = "#0F5B21";
    private final String LIGHT_GREEN = "#D2F4D6";
    private final String TEXT_GRAY = "#757575";
    private final String BORDER_COLOR = "#E0E0E0";
    private final String BG_COLOR = "#F5F5F5";

    public DonationDetailUI(User user, Donation donation) {
        super(user);
        this.donation = donation;
    }

    @Override
    public Parent getSceneContent(Stage stage) {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // --- TOP BAR ---
        topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: white;");

        Label backArrow = new Label("←");
        backArrow.setFont(Font.font("System", FontWeight.BOLD, 20));
        backArrow.setTextFill(Color.web(DARK_GREEN));
        backArrow.setStyle("-fx-cursor: hand;");
        backArrow.setOnMouseClicked(e -> {
            if (getUser() instanceof Donator) {
                Navigator.navigate(stage, new CatalogUI(getUser()));
            } else {
                Navigator.navigate(stage, new ClaimDonationUI(getUser()));
            }
        });

        Label logoLabel = new Label("DONE-ATE");
        logoLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        logoLabel.setTextFill(Color.web(DARK_GREEN));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Circle profileCircle = new Circle(18, Color.web("#E0E0E0"));
        topBar.getChildren().addAll(backArrow, logoLabel, spacer, profileCircle);

        // --- CONTENT AREA (SCROLLABLE) ---
        VBox scrollContent = new VBox();
        scrollContent.setAlignment(Pos.TOP_CENTER);

        // Hero Image Section
        StackPane heroSection = new StackPane();
        heroSection.setPrefHeight(300);
        heroSection.setMaxHeight(300);

        ImageView foodImg = new ImageView();
        foodImg.setFitWidth(800); 
        foodImg.setFitHeight(300);
        foodImg.setPreserveRatio(false);

        if (donation != null && donation.getDish() != null && donation.getDish().getImagePath() != null) {
            try {
                File file = new File(donation.getDish().getImagePath());
                if (file.exists()) {
                    foodImg.setImage(new Image(file.toURI().toString()));
                }
            } catch (Exception e) {}
        }
        heroSection.getChildren().add(foodImg);

        // Info Card
        VBox infoCard = new VBox(25);
        infoCard.setPadding(new Insets(30, 40, 30, 40));
        infoCard.setStyle("-fx-background-color: white; -fx-background-radius: 20 20 0 0; -fx-translate-y: -20;");
        infoCard.setMaxWidth(800);

        Label title = new Label(donation != null && donation.getDish() != null ? donation.getDish().getName() : "Donasi Makanan");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setWrapText(true);

        Label statusBadge = new Label(donation != null ? donation.getStatus().toUpperCase() : "AKTIF");
        statusBadge.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-text-fill: " + DARK_GREEN + "; -fx-padding: 5 12; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 12;");

        HBox titleRow = new HBox(15, title, statusBadge);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label desc = new Label("Donasi ini dibagikan oleh " + (getUser() instanceof Donator ? "Anda" : "Donator Terverifikasi") + ". Makanan ini telah melewati pengecekan kualitas standar DONE-ATE.");
        desc.setFont(Font.font(14));
        desc.setTextFill(Color.web(TEXT_GRAY));
        desc.setWrapText(true);

        // Details Grid
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(20);
        
        grid.add(detailItem("🕒 Waktu Masak", "2 Jam yang lalu"), 0, 0);
        grid.add(detailItem("⌛ Kedaluwarsa", "6 Jam lagi"), 1, 0);
        grid.add(detailItem("📍 Lokasi", "Sukajadi, Bandung"), 0, 1);
        grid.add(detailItem("📦 Porsi", "5 Orang"), 1, 1);

        Separator sep = new Separator();
        sep.setPadding(new Insets(10, 0, 10, 0));

        infoCard.getChildren().addAll(titleRow, desc, grid, sep);

        // Action Buttons based on Role
        if (getUser() instanceof Recipient) {
            Button claimBtn = new Button("Klaim Donasi Sekarang");
            claimBtn.setStyle("-fx-background-color: " + DARK_GREEN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 12; -fx-cursor: hand;");
            claimBtn.setMaxWidth(Double.MAX_VALUE);
            claimBtn.setPrefHeight(55);
            infoCard.getChildren().add(claimBtn);
        }

        scrollContent.getChildren().addAll(heroSection, infoCard);
        
        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG_COLOR + ";");
        
        root.getChildren().addAll(topBar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return root;
    }

    private VBox detailItem(String label, String value) {
        VBox box = new VBox(4);
        Label lbl = new Label(label);
        lbl.setFont(Font.font(12));
        lbl.setTextFill(Color.web(TEXT_GRAY));
        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, 14));
        box.getChildren().addAll(lbl, val);
        return box;
    }

    @Override
    public void showUI() {
        initJFX();
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("DONE-ATE - Detail Donasi");
            Scene scene = new Scene(getSceneContent(stage), 1920, 1080);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        });
    }
}
