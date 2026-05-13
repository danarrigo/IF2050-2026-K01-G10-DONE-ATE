package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import javafx.application.Platform;

import java.util.List;

public abstract class UI {
    private static boolean jfxInitialized = false;
    private List<Donation> donations;
    private User user;

    public UI(User user) {
        this.user = user;
    }

    protected void initJFX() {
        if (!jfxInitialized) {
            try {
                Platform.startup(() -> {});
                jfxInitialized = true;
            } catch (IllegalStateException e) {
                // Platform already started
                jfxInitialized = true;
            }
        }
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public abstract void showUI();
}
