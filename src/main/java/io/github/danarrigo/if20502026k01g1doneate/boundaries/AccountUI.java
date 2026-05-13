package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.enums.DonatorType;
import io.github.danarrigo.if20502026k01g1doneate.enums.RecipientType;
import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AccountUI extends UI {

    private static final String DARK_GREEN   = "#0F5B21";
    private static final String TEXT_GRAY    = "#555555";
    private static final String BORDER_COLOR = "#E0E0E0";
    private static final String BG_COLOR     = "#FAFAFA";
    private static final String BASE_URL     = "http://localhost:8080";

    private boolean isEditMode = false;
    private VBox contentContainer;
    private User fullUser;
    private Label statusLabel;

    public AccountUI(User user) {
        super(user);
        this.fullUser = user;
    }

    @Override
    public void showUI() {
        initJFX();
        Platform.runLater(this::createAndShowStage);
    }

    @Override
    public Parent getSceneContent(Stage stage) {
        if (fullUser == null || fullUser.getEmail() == null) {
            fetchFullProfile();
        }

        HBox root = new HBox();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        String username = SessionManager.getInstance().getUsername();
        String role = SessionManager.getInstance().getRole();

        VBox sidebar = buildSidebar(stage, username, role);
        
        contentContainer = new VBox();
        ScrollPane contentScroll = new ScrollPane(contentContainer);
        contentScroll.setFitToWidth(true);
        contentScroll.setStyle("-fx-background-color: " + BG_COLOR + "; -fx-background: " + BG_COLOR + ";");
        HBox.setHgrow(contentScroll, Priority.ALWAYS);

        refreshContent(stage);

        root.getChildren().addAll(sidebar, contentScroll);
        playAnimation(root);
        return root;
    }

    private void fetchFullProfile() {
        new Thread(() -> {
            try {
                String token = SessionManager.getInstance().getToken();
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/users/me"))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(response.body());
                    
                    Platform.runLater(() -> {
                        if (SessionManager.getInstance().getRole().equalsIgnoreCase("DONATOR")) {
                            Donator d = new Donator();
                            d.setUsername(node.get("username").asText());
                            d.setEmail(node.has("email") ? node.get("email").asText() : "");
                            d.setPhoneNumber(node.has("phoneNumber") ? node.get("phoneNumber").asText() : "");
                            d.setAddress(node.has("address") ? node.get("address").asText() : "");
                            if (node.has("donatorType")) {
                                d.setDonatorType(DonatorType.valueOf(node.get("donatorType").asText()));
                            }
                            fullUser = d;
                        } else {
                            Recipient r = new Recipient();
                            r.setUsername(node.get("username").asText());
                            r.setEmail(node.has("email") ? node.get("email").asText() : "");
                            r.setPhoneNumber(node.has("phoneNumber") ? node.get("phoneNumber").asText() : "");
                            r.setAddress(node.has("address") ? node.get("address").asText() : "");
                            if (node.has("fullName") && !node.get("fullName").isNull()) {
                                r.setFullName(node.get("fullName").asText());
                            }
                            if (node.has("recipientType") && !node.get("recipientType").isNull()) {
                                r.setRecipientType(RecipientType.valueOf(node.get("recipientType").asText()));
                            }
                            if (node.has("operationalTimeStart") && !node.get("operationalTimeStart").isNull()) {
                                r.setOperationalTimeStart(LocalTime.parse(node.get("operationalTimeStart").asText()));
                            }
                            if (node.has("operationalTimeEnd") && !node.get("operationalTimeEnd").isNull()) {
                                r.setOperationalTimeEnd(LocalTime.parse(node.get("operationalTimeEnd").asText()));
                            }
                            fullUser = r;
                        }
                        setUser(fullUser);
                        // Trigger refresh if stage is showing
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void refreshContent(Stage stage) {
        contentContainer.getChildren().clear();
        contentContainer.setPadding(new Insets(60, 80, 60, 80));
        contentContainer.setSpacing(32);

        String username = SessionManager.getInstance().getUsername();
        String role = SessionManager.getInstance().getRole();
        User u = fullUser != null ? fullUser : getUser();

        // Back Button
        Button backBtn = new Button("← Kembali");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + DARK_GREEN + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0;");
        backBtn.setOnAction(e -> Navigator.navigate(stage, u instanceof Donator ? new CatalogUI(u) : new RecipientCatalogUI(u)));

        Label pageTitle = new Label("Profil Saya");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 36));

        HBox headerRow = new HBox(20, pageTitle);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label();
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
        headerRow.getChildren().add(statusLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button editToggleBtn = new Button(isEditMode ? "Batal" : "Edit Profil");
        editToggleBtn.setStyle("-fx-background-color: " + (isEditMode ? "#EEE" : DARK_GREEN) + "; -fx-text-fill: " + (isEditMode ? "#333" : "white") + "; -fx-font-weight: bold; -fx-padding: 10 25; -fx-background-radius: 8; -fx-cursor: hand;");
        editToggleBtn.setOnAction(e -> {
            isEditMode = !isEditMode;
            refreshContent(stage);
        });
        
        headerRow.getChildren().addAll(spacer, editToggleBtn);

        VBox profileCard;
        if (isEditMode) {
            profileCard = buildEditCard(stage);
        } else {
            profileCard = buildInfoCard("Informasi Akun", new String[][]{
                    {"Username",        username},
                    {"Peran",           formatRole(role)},
                    {"Email",           u != null ? u.getEmail() : "-"},
                    {"Nomor Telepon",   u != null ? u.getPhoneNumber() : "-"},
                    {"Alamat",          u != null ? u.getAddress() : "-"}
            });
        }

        contentContainer.getChildren().addAll(backBtn, headerRow, profileCard);

        if (!isEditMode) {
            VBox roleCard = buildRoleCard(role);
            if (roleCard != null) contentContainer.getChildren().add(roleCard);
        }
    }

    private VBox buildEditCard(Stage stage) {
        VBox card = new VBox(25);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        TextField emailF = new TextField(fullUser != null ? fullUser.getEmail() : "");
        TextField phoneF = new TextField(fullUser != null ? fullUser.getPhoneNumber() : "");
        TextArea addrF = new TextArea(fullUser != null ? fullUser.getAddress() : "");
        addrF.setPrefRowCount(3);
        
        TextField fullNameF = null;
        if (fullUser instanceof Recipient r) {
            fullNameF = new TextField(r.getFullName() != null ? r.getFullName() : "");
            styleControl(fullNameF);
        }
        
        styleControl(emailF);
        styleControl(phoneF);
        addrF.setStyle("-fx-background-radius: 10; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-padding: 10;");

        if (fullNameF != null) {
            card.getChildren().addAll(new Label("Nama Lengkap"), fullNameF);
        }
        
        card.getChildren().addAll(
            new Label("Email"), emailF,
            new Label("Nomor Telepon"), phoneF,
            new Label("Alamat"), addrF
        );

        ComboBox<DonatorType> typeBox = null;
        if (fullUser instanceof Donator d) {
            typeBox = new ComboBox<>(FXCollections.observableArrayList(DonatorType.values()));
            typeBox.setValue(d.getDonatorType());
            typeBox.setMaxWidth(Double.MAX_VALUE);
            card.getChildren().addAll(new Label("Tipe Donator"), typeBox);
        }

        final ComboBox<DonatorType> finalTypeBox = typeBox;
        
        ComboBox<RecipientType> rTypeBox = null;
        TextField startF = null;
        TextField endF = null;
        if (fullUser instanceof Recipient r) {
            rTypeBox = new ComboBox<>(FXCollections.observableArrayList(RecipientType.values()));
            rTypeBox.setValue(r.getRecipientType());
            rTypeBox.setMaxWidth(Double.MAX_VALUE);
            
            startF = new TextField(r.getOperationalTimeStart() != null ? r.getOperationalTimeStart().toString() : "08:00");
            endF = new TextField(r.getOperationalTimeEnd() != null ? r.getOperationalTimeEnd().toString() : "17:00");
            
            styleControl(startF);
            styleControl(endF);
            
            card.getChildren().addAll(
                new Label("Tipe Penerima"), rTypeBox,
                new Label("Jam Operasional (Mulai - Format HH:mm)"), startF,
                new Label("Jam Operasional (Selesai - Format HH:mm)"), endF
            );
        }

        Button saveBtn = new Button("Simpan Perubahan");
        saveBtn.setStyle("-fx-background-color: " + DARK_GREEN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 10; -fx-cursor: hand;");
        
        final ComboBox<RecipientType> finalRTypeBox = rTypeBox;
        final TextField finalStartF = startF;
        final TextField finalEndF = endF;
        final TextField finalFullNameF = fullNameF;
        
        saveBtn.setOnAction(e -> {
            LocalTime start = null;
            LocalTime end = null;
            try {
                if (finalStartF != null) start = LocalTime.parse(finalStartF.getText());
                if (finalEndF != null) end = LocalTime.parse(finalEndF.getText());
            } catch (Exception ex) {
                showStatus("Format jam tidak valid. Gunakan HH:mm", true);
                return;
            }
            handleUpdateProfile(stage, emailF.getText(), phoneF.getText(), addrF.getText(), 
                               finalTypeBox != null ? finalTypeBox.getValue() : null,
                               finalFullNameF != null ? finalFullNameF.getText() : null,
                               finalRTypeBox != null ? finalRTypeBox.getValue() : null,
                               start, end);
        });

        card.getChildren().add(saveBtn);
        return card;
    }

    private void styleControl(Control c) {
        c.setStyle("-fx-background-radius: 10; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-padding: 12; -fx-font-size: 14;");
        c.setMaxWidth(Double.MAX_VALUE);
    }

    private void handleUpdateProfile(Stage stage, String email, String phone, String address, DonatorType dType, String fullName, RecipientType rType, LocalTime start, LocalTime end) {
        new Thread(() -> {
            try {
                String token = SessionManager.getInstance().getToken();
                StringBuilder bodyBuilder = new StringBuilder();
                bodyBuilder.append("{");
                bodyBuilder.append(String.format("\"email\":\"%s\",\"phoneNumber\":\"%s\",\"address\":\"%s\"", email, phone, address));
                if (dType != null) bodyBuilder.append(String.format(",\"donatorType\":\"%s\"", dType.name()));
                if (fullName != null) bodyBuilder.append(String.format(",\"fullName\":\"%s\"", fullName));
                if (rType != null) bodyBuilder.append(String.format(",\"recipientType\":\"%s\"", rType.name()));
                if (start != null) bodyBuilder.append(String.format(",\"operationalTimeStart\":\"%s\"", start.toString()));
                if (end != null) bodyBuilder.append(String.format(",\"operationalTimeEnd\":\"%s\"", end.toString()));
                bodyBuilder.append("}");

                String body = bodyBuilder.toString();

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/users/me"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .PUT(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        if (fullUser != null) {
                            fullUser.setEmail(email);
                            fullUser.setPhoneNumber(phone);
                            fullUser.setAddress(address);
                            if (fullUser instanceof Donator d) d.setDonatorType(dType);
                            if (fullUser instanceof Recipient r) {
                                r.setFullName(fullName);
                                r.setRecipientType(rType);
                                r.setOperationalTimeStart(start);
                                r.setOperationalTimeEnd(end);
                            }
                        }
                        isEditMode = false;
                        refreshContent(stage);
                        showStatus("Profil berhasil diperbarui. Notifikasi telah dikirim ke Inbox.", false);
                    } else {
                        showStatus("Gagal memperbarui profil. Silakan coba lagi.", true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showStatus(String msg, boolean isError) {
        if (statusLabel == null) return;
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-background-color: " + (isError ? "#FFEBEE" : "#E8F5E9") + "; " +
                           "-fx-text-fill: " + (isError ? "#C62828" : "#2E7D32") + "; " +
                           "-fx-padding: 8 16; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 14px;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), statusLabel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        new Thread(() -> {
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                FadeTransition ftOut = new FadeTransition(Duration.seconds(0.5), statusLabel);
                ftOut.setFromValue(1);
                ftOut.setToValue(0);
                ftOut.setOnFinished(e -> {
                    statusLabel.setVisible(false);
                    statusLabel.setManaged(false);
                });
                ftOut.play();
            });
        }).start();
    }

    private void createAndShowStage() {
        Stage stage = new Stage();
        stage.setTitle("DONE-ATE - Akun Saya");
        stage.setMaximized(true);
        Scene scene = new Scene(getSceneContent(stage));
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    // ─── Sidebar ───────────────────────────────────────────────────────────────

    private VBox buildSidebar(Stage stage, String username, String role) {
        VBox sidebar = new VBox(24);
        sidebar.setPrefWidth(300);
        sidebar.setMinWidth(300);
        sidebar.setPadding(new Insets(60, 30, 40, 30));
        sidebar.setStyle("-fx-background-color: " + DARK_GREEN + ";");
        sidebar.setAlignment(Pos.TOP_CENTER);

        // Avatar circle with initials
        StackPane avatar = new StackPane();
        Circle circle = new Circle(50);
        circle.setFill(Color.web("#ffffff", 0.2));
        circle.setStroke(Color.web("#ffffff", 0.5));
        circle.setStrokeWidth(2);
        String initials = username != null && username.length() >= 2
                ? username.substring(0, 2).toUpperCase()
                : (username != null ? username.toUpperCase() : "??");
        Label initialsLabel = new Label(initials);
        initialsLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        initialsLabel.setStyle("-fx-text-fill: white;");
        avatar.getChildren().addAll(circle, initialsLabel);

        Label usernameLabel = new Label(username);
        usernameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        usernameLabel.setStyle("-fx-text-fill: white;");
        usernameLabel.setWrapText(true);
        usernameLabel.setAlignment(Pos.CENTER);

        Label roleBadge = new Label(formatRole(role));
        roleBadge.setStyle(
                "-fx-background-color: rgba(255,255,255,0.2);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20px;" +
                "-fx-padding: 4 16 4 16;"
        );

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.2);");
        sep.setMaxWidth(Double.MAX_VALUE);

        HBox profileMenu = buildMenuEntry("Profil Saya", true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Keluar");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 14 0 14 0;"
        );
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(logoutBtn.getStyle().replace("0.15", "0.30")));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(logoutBtn.getStyle().replace("0.30", "0.15")));
        logoutBtn.setOnAction(e -> handleLogout(stage));

        sidebar.getChildren().addAll(avatar, usernameLabel, roleBadge, sep, profileMenu, spacer, logoutBtn);
        return sidebar;
    }

    private HBox buildMenuEntry(String text, boolean active) {
        HBox entry = new HBox(12);
        entry.setAlignment(Pos.CENTER_LEFT);
        entry.setPadding(new Insets(12, 16, 12, 16));
        entry.setMaxWidth(Double.MAX_VALUE);
        entry.setStyle(
                "-fx-background-color: " + (active ? "rgba(255,255,255,0.15)" : "transparent") + ";" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;"
        );
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;" + (active ? " -fx-font-weight: bold;" : ""));
        entry.getChildren().add(label);
        return entry;
    }

    private VBox buildInfoCard(String title, String[][] rows) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 12px;" +
                "-fx-background-radius: 12px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 20, 0, 0, 4);"
        );

        Label cardTitle = new Label(title);
        cardTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        cardTitle.setStyle("-fx-text-fill: " + DARK_GREEN + ";");

        Separator sep = new Separator();

        VBox fields = new VBox(16);
        for (String[] row : rows) {
            HBox fieldRow = new HBox(20);
            fieldRow.setAlignment(Pos.TOP_LEFT);

            Label key = new Label(row[0]);
            key.setPrefWidth(160);
            key.setMinWidth(160);
            key.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

            String val = (row[1] != null && !row[1].isEmpty() && !row[1].equals("null")) ? row[1] : "-";
            Label value = new Label(val);
            value.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_GRAY + ";");
            value.setWrapText(true);
            HBox.setHgrow(value, Priority.ALWAYS);

            fieldRow.getChildren().addAll(key, value);
            fields.getChildren().add(fieldRow);
        }

        card.getChildren().addAll(cardTitle, sep, fields);
        return card;
    }

    private VBox buildRoleCard(String role) {
        if (role == null) return null;
        User u = fullUser != null ? fullUser : getUser();
        return switch (role.toUpperCase()) {
            case "DONATOR" -> buildInfoCard("Informasi Donator", new String[][]{
                    {"Tipe Donator", u instanceof Donator d ? (d.getDonatorType() != null ? d.getDonatorType().name() : "-") : "-"},
                    {"Riwayat Donasi", "Lihat di menu Riwayat"}
            });
            case "RECIPIENT" -> buildInfoCard("Informasi Penerima", new String[][]{
                    {"Nama Lengkap",    u instanceof Recipient r ? (r.getFullName() != null ? r.getFullName() : "-") : "-"},
                    {"Tipe Penerima",   u instanceof Recipient r ? (r.getRecipientType() != null ? r.getRecipientType().name() : "-") : "-"},
                    {"Jam Operasional", u instanceof Recipient r ? (r.getOperationalTimeStart() != null ? r.getOperationalTimeStart() + " - " + r.getOperationalTimeEnd() : "-") : "-"}
            });
            default -> null;
        };
    }

    private void handleLogout(Stage stage) {
        SessionManager.getInstance().clearSession();
        Navigator.navigate(stage, new LoginUI());
    }

    private String formatRole(String role) {
        if (role == null) return "-";
        return switch (role.toUpperCase()) {
            case "DONATOR"   -> "Donator";
            case "RECIPIENT" -> "Penerima";
            default          -> role;
        };
    }

    private void playAnimation(HBox root) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        TranslateTransition tt = new TranslateTransition(Duration.millis(500), root);
        tt.setFromY(20);
        tt.setToY(0);
        tt.play();
    }

    public static void main(String[] args) {
        new AccountUI(null).showUI();
    }
}
