package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Notification;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.enums.NotificationType;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InboxUI extends UI {
    private Stage stage;

    private int currentPage = 0;
    private final int PAGE_SIZE = 10;
    private String currentFilter = "ALL";

    private VBox notificationListContainer;
    private Label lblEmptyInbox;
    private Button btnLoadMore;
    private HBox filterBox;

    public InboxUI(User user) {
        super(user);
    }

    @Override
    public Parent getSceneContent(Stage stage) {
        this.stage = stage;
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #FBF9F8;");

        Label title = new Label("Kotak Masuk");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label subtitle = new Label("Kelola pesan dan pemberitahuan bantuan pangan Anda.");
        subtitle.setStyle("-fx-text-fill: #666666;");

        filterBox = new HBox(10);
        setupFilterButtons();

        notificationListContainer = new VBox(15);

        lblEmptyInbox = new Label("Belum ada notifikasi saat ini.");
        lblEmptyInbox.setStyle("-fx-font-style: italic; -fx-text-fill: #999999;");
        lblEmptyInbox.setVisible(false);
        lblEmptyInbox.setManaged(false);

        btnLoadMore = new Button("Lihat Selengkapnya");
        btnLoadMore.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #1B5E20; -fx-font-weight: bold; -fx-cursor: hand;");
        btnLoadMore.setVisible(false);
        btnLoadMore.setManaged(false);
        btnLoadMore.setOnAction(e -> {
            currentPage++;
            fetchNotifications(false);
        });

        VBox contentWrapper = new VBox(20, lblEmptyInbox, notificationListContainer, btnLoadMore);
        contentWrapper.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(contentWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #FAFAFA;");

        root.getChildren().addAll(title, subtitle, filterBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        HBox bottomNav = Navigator.createBottomNav(stage, getUser(), "INBOX");
        root.getChildren().add(bottomNav);

        fetchNotifications(true);
        
        return root;
    }

    @Override
    public void showUI() {
        initJFX();
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("DONE-ATE - Kotak Masuk");
            
            Scene scene = new Scene(getSceneContent(stage), 1920, 1080);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        });
    }

    private void setupFilterButtons() {
        filterBox.getChildren().clear();

        Button btnSemua = createFilterButton("Semua", currentFilter.equals("ALL"));
        Button btnBelumDibaca = createFilterButton("Belum Dibaca", currentFilter.equals("UNREAD"));
        Button btnDonasi = createFilterButton("Donasi", currentFilter.equals("DONASI"));
        Button btnSistem = createFilterButton("Sistem", currentFilter.equals("SISTEM"));

        btnSemua.setOnAction(e -> applyFilter("ALL"));
        btnBelumDibaca.setOnAction(e -> applyFilter("UNREAD"));
        btnDonasi.setOnAction(e -> applyFilter("DONASI"));
        btnSistem.setOnAction(e -> applyFilter("SISTEM"));

        filterBox.getChildren().addAll(btnSemua, btnBelumDibaca, btnDonasi, btnSistem);
    }

    private void applyFilter(String filterName) {
        currentFilter = filterName;
        currentPage = 0;
        setupFilterButtons();
        fetchNotifications(true);
    }

    private void fetchNotifications(boolean clearExisting) {
        if (clearExisting) {
            notificationListContainer.getChildren().clear();
            lblEmptyInbox.setVisible(false);
            lblEmptyInbox.setManaged(false);
            btnLoadMore.setVisible(false);
            btnLoadMore.setManaged(false);
        }

        CompletableFuture.runAsync(() -> {
            try {
                String token = SessionManager.getInstance().getToken();
                HttpClient client = HttpClient.newHttpClient();
                String url = String.format("http://localhost:8080/api/notifications/user/%s?filter=%s&page=%d&size=%d",
                        getUser().getUsername(), currentFilter, currentPage, PAGE_SIZE);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());

                    JsonNode rootNode = mapper.readTree(response.body());
                    JsonNode contentNode = rootNode.get("content");
                    boolean isLast = rootNode.get("last").asBoolean();

                    List<Notification> fetchedItems = mapper.convertValue(contentNode,
                            new TypeReference<List<Notification>>() {
                            });

                    Platform.runLater(() -> {
                        if (currentPage == 0 && fetchedItems.isEmpty()) {
                            lblEmptyInbox.setVisible(true);
                            lblEmptyInbox.setManaged(true);
                        } else {
                            renderNotificationItems(fetchedItems);
                            btnLoadMore.setVisible(!isLast);
                            btnLoadMore.setManaged(!isLast);
                        }
                    });
                } else {
                    System.err.println("API Error: " + response.statusCode() + " - " + response.body());
                    Platform.runLater(() -> {
                        lblEmptyInbox.setText("Error " + response.statusCode() + ": Gagal memuat data dari server.");
                        lblEmptyInbox.setVisible(true);
                        lblEmptyInbox.setManaged(true);
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblEmptyInbox.setText("Terjadi kesalahan koneksi ke server.");
                    lblEmptyInbox.setVisible(true);
                    lblEmptyInbox.setManaged(true);
                });
            }
        });
    }

    private void renderNotificationItems(List<Notification> newItems) {
        for (Notification notif : newItems) {
            HBox card = new HBox(15);
            card.setPadding(new Insets(15));
            card.setAlignment(Pos.CENTER_LEFT);

            String readStyle = "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-cursor: hand;";
            String unreadStyle = "-fx-background-color: #E8F5E9; -fx-background-radius: 10; -fx-border-color: #A5D6A7; -fx-border-left-color: #1B5E20; -fx-border-left-width: 5; -fx-border-radius: 10; -fx-cursor: hand;";
            card.setStyle(notif.isRead() ? readStyle : unreadStyle);

            StackPane iconBox = createIconBox(notif);

            VBox textContainer = new VBox(5);

            Label titleLabel = new Label(notif.getTitle());
            titleLabel.setStyle("-fx-font-weight: " + (notif.isRead() ? "normal" : "bold")
                    + "; -fx-font-size: 14px; -fx-text-fill: #333333;");

            Label msgLabel = new Label(notif.getMessageBody());
            msgLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 13px; -fx-font-weight: "
                    + (notif.isRead() ? "normal" : "bold") + ";");
            msgLabel.setWrapText(true);
            msgLabel.setMaxWidth(380);

            textContainer.getChildren().addAll(titleLabel, msgLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            VBox timeContainer = new VBox();
            timeContainer.setAlignment(Pos.TOP_RIGHT);
            Label timeLabel = new Label(getRelativeTime(notif.getTimeStamp()));
            timeLabel.setStyle("-fx-text-fill: #999999; -fx-font-size: 12px;");
            timeContainer.getChildren().add(timeLabel);

            card.getChildren().addAll(iconBox, textContainer, spacer, timeContainer);

            card.setOnMouseClicked(e -> {
                if (!notif.isRead()) {
                    markAsReadInBackend(notif.getNotificationId());
                    notif.setRead(true);

                    card.setStyle(readStyle);
                    titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px; -fx-text-fill: #333333;");
                    msgLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 13px; -fx-font-weight: normal;");
                }
                openNotification(notif);
            });

            notificationListContainer.getChildren().add(card);
        }
    }

    private void markAsReadInBackend(java.util.UUID notificationId) {
        CompletableFuture.runAsync(() -> {
            try {
                String token = SessionManager.getInstance().getToken();
                HttpClient client = HttpClient.newHttpClient();
                String url = "http://localhost:8080/api/notifications/" + notificationId + "/read";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception ex) {
                System.err.println("Gagal update status read.");
            }
        });
    }

    public void openNotification(Notification notification) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detail Notifikasi");

        DialogPane dialogPane = new DialogPane();
        dialog.setDialogPane(dialogPane);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        StackPane iconPane = createIconBox(notification);
        grid.add(iconPane, 0, 0, 1, 2);
        GridPane.setVgrow(iconPane, Priority.ALWAYS);

        Label titleLabel = new Label(notification.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333333;");
        grid.add(titleLabel, 1, 0);

        Label msgLabel = new Label(notification.getMessageBody());
        msgLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(350);
        grid.add(msgLabel, 1, 1);

        dialogPane.setContent(grid);

        ButtonType okButtonType = new ButtonType("Tutup", ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(okButtonType);

        Node okButton = dialogPane.lookupButton(okButtonType);
        if (okButton instanceof Button) {
            okButton.setStyle(
                    "-fx-background-color: #1B5E20; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 7 20; -fx-cursor: hand; -fx-font-weight: bold;");

            okButton.setOnMouseEntered(e -> okButton.setStyle(
                    "-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 7 20; -fx-cursor: hand; -fx-font-weight: bold;"));
            okButton.setOnMouseExited(e -> okButton.setStyle(
                    "-fx-background-color: #1B5E20; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 7 20; -fx-cursor: hand; -fx-font-weight: bold;"));
        }

        dialogPane.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E0E0E0; -fx-border-radius: 10;");

        dialog.showAndWait();

        if (notification.getRelatedDonationId() != null) {
            fetchDonationAndOpenDetail(notification.getRelatedDonationId());
        }
    }

    private void fetchDonationAndOpenDetail(java.util.UUID donationId) {
        CompletableFuture.runAsync(() -> {
            try {
                String token = SessionManager.getInstance().getToken();
                HttpClient client = HttpClient.newHttpClient();
                String url = "http://localhost:8080/api/donations/" + donationId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                    Donation donation = mapper.readValue(response.body(), Donation.class);

                    Platform.runLater(() -> {
                        Navigator.navigate(stage, new DonationDetailUI(getUser(), donation));
                    });
                } else {
                    System.err.println("Gagal memuat detail donasi: " + response.statusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Button createFilterButton(String text, boolean isActive) {
        Button btn = new Button(text);
        if (isActive) {
            btn.setStyle(
                    "-fx-background-color: #1B5E20; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
        } else {
            btn.setStyle(
                    "-fx-background-color: #E8F5E9; -fx-text-fill: #1B5E20; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
        }
        return btn;
    }

    private String getRelativeTime(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(timestamp, now);

        if (duration.toMinutes() < 1)
            return "Baru saja";
        if (duration.toMinutes() < 60)
            return duration.toMinutes() + " menit lalu";
        if (duration.toHours() < 24)
            return duration.toHours() + " jam lalu";
        if (duration.toDays() == 1)
            return "Kemarin";

        return timestamp.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    private StackPane createIconBox(Notification notif) {
        StackPane iconPane = new StackPane();
        Circle bg = new Circle(20);
        Label iconLabel = new Label();

        if (notif.getType() == NotificationType.DONASI) {
            bg.setFill(Color.web("#E8F5E9"));
            iconLabel.setText("💖");
            iconLabel.setStyle("-fx-font-size: 18px;");
        } else {
            bg.setFill(Color.web("#F5F5F5"));
            iconLabel.setText("⏱️");
            iconLabel.setStyle("-fx-font-size: 18px;");
        }

        iconPane.getChildren().addAll(bg, iconLabel);
        return iconPane;
    }
}