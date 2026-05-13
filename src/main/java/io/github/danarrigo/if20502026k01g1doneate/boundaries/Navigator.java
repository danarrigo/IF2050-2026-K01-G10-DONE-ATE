package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Navigator {

    private static final String DARK_GREEN = "#0F5B21";
    private static final String LIGHT_GREEN = "#D2F4D6";
    private static final String TEXT_GRAY = "#757575";
    private static final String BORDER_COLOR = "#E0E0E0";

    public static HBox createBottomNav(Stage currentStage, User user, String activeTab) {
        HBox nav = new HBox();
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(10));
        nav.setSpacing(35);
        nav.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + " transparent transparent transparent;");

        nav.getChildren().add(createNavItem("🏠", "Home", "HOME".equals(activeTab), currentStage, user));
        nav.getChildren().add(createNavItem("🍱", "Catalog", "CATALOG".equals(activeTab), currentStage, user));

        nav.getChildren().addAll(
                createNavItem("✉", "Inbox", "INBOX".equals(activeTab), currentStage, user),
                createNavItem("📜", "History", "HISTORY".equals(activeTab), currentStage, user),
                createNavItem("👤", "Account", "ACCOUNT".equals(activeTab), currentStage, user)
        );
        return nav;
    }

    private static VBox createNavItem(String icon, String label, boolean active, Stage stage, User user) {
        VBox item = new VBox(2);
        item.setAlignment(Pos.CENTER);
        item.setPadding(new Insets(5, 10, 5, 10));
        item.setStyle("-fx-cursor: hand;");

        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(20));
        Label textLbl = new Label(label);
        textLbl.setFont(Font.font(10));

        if (active) {
            item.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 12px;");
            iconLbl.setTextFill(Color.web(DARK_GREEN));
            textLbl.setTextFill(Color.web(DARK_GREEN));
            textLbl.setFont(Font.font("System", FontWeight.BOLD, 10));
        } else {
            iconLbl.setTextFill(Color.web(TEXT_GRAY));
            textLbl.setTextFill(Color.web(TEXT_GRAY));
        }

        item.setOnMouseClicked(e -> {
            if (active) return;
            
            UI targetUI;
            switch (label) {
                case "Home":
                    if (user instanceof Donator) targetUI = new CatalogUI(user);
                    else if (user instanceof Recipient) targetUI = new RecipientCatalogUI(user);
                    else targetUI = new CatalogUI(user);
                    break;
                case "Catalog":
                    if (user instanceof Donator) targetUI = new CatalogUI(user);
                    else targetUI = new RecipientCatalogUI(user);
                    break;
                case "Inbox":
                    targetUI = new InboxUI(user);
                    break;
                case "History":
                    targetUI = new HistoryUI(user);
                    break;
                case "Account":
                    targetUI = new AccountUI(user);
                    break;
                default:
                    return;
            }

            Parent newContent = targetUI.getSceneContent(stage);
            Parent oldContent = stage.getScene().getRoot();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), oldContent);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                stage.getScene().setRoot(newContent);
                newContent.setOpacity(0.0);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newContent);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        });

        item.getChildren().addAll(iconLbl, textLbl);
        return item;
    }

    public static void navigate(Stage stage, UI targetUI) {
        Parent newContent = targetUI.getSceneContent(stage);
        Parent oldContent = stage.getScene().getRoot();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), oldContent);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            stage.getScene().setRoot(newContent);
            newContent.setOpacity(0.0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newContent);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }
}
