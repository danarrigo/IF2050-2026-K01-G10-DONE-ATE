package io.github.danarrigo.if20502026k01g1doneate.dtos;

import java.util.UUID;

public class QCFormData {
    private UUID dishId;
    private boolean freshScent;
    private boolean noSpoilage;
    private boolean properlyCooked;
    private boolean hasExpiredIngredients;
    private boolean noForeignObjects;
    private boolean safeTemperature;
    private boolean normalColor;
    private boolean goodTexture;
    private boolean safeToEat;
    private boolean presentable;

    public QCFormData() {
    }

    public boolean isPassed() {
        return freshScent && noSpoilage && properlyCooked && !hasExpiredIngredients &&
                noForeignObjects && safeTemperature && normalColor && goodTexture &&
                safeToEat && presentable;
    }

    public UUID getDishId() {
        return dishId;
    }

    public void setDishId(UUID dishId) {
        this.dishId = dishId;
    }

    public boolean isFreshScent() {
        return freshScent;
    }

    public void setFreshScent(boolean freshScent) {
        this.freshScent = freshScent;
    }

    public boolean isNoSpoilage() {
        return noSpoilage;
    }

    public void setNoSpoilage(boolean noSpoilage) {
        this.noSpoilage = noSpoilage;
    }

    public boolean isProperlyCooked() {
        return properlyCooked;
    }

    public void setProperlyCooked(boolean properlyCooked) {
        this.properlyCooked = properlyCooked;
    }

    public boolean isHasExpiredIngredients() {
        return hasExpiredIngredients;
    }

    public void setHasExpiredIngredients(boolean hasExpiredIngredients) {
        this.hasExpiredIngredients = hasExpiredIngredients;
    }

    public boolean isNoForeignObjects() {
        return noForeignObjects;
    }

    public void setNoForeignObjects(boolean noForeignObjects) {
        this.noForeignObjects = noForeignObjects;
    }

    public boolean isSafeTemperature() {
        return safeTemperature;
    }

    public void setSafeTemperature(boolean safeTemperature) {
        this.safeTemperature = safeTemperature;
    }

    public boolean isNormalColor() {
        return normalColor;
    }

    public void setNormalColor(boolean normalColor) {
        this.normalColor = normalColor;
    }

    public boolean isGoodTexture() {
        return goodTexture;
    }

    public void setGoodTexture(boolean goodTexture) {
        this.goodTexture = goodTexture;
    }

    public boolean isSafeToEat() {
        return safeToEat;
    }

    public void setSafeToEat(boolean safeToEat) {
        this.safeToEat = safeToEat;
    }

    public boolean isPresentable() {
        return presentable;
    }

    public void setPresentable(boolean presentable) {
        this.presentable = presentable;
    }
}
