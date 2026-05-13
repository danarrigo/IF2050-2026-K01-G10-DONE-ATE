package io.github.danarrigo.if20502026k01g1doneate.dtos;

import java.time.LocalDateTime;

public class CatalogItemRequest {
    private String dishName;
    private String imagePath;
    private long expiresInMinutes;
    private LocalDateTime timeCooked;

    public CatalogItemRequest() {}

    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public long getExpiresInMinutes() { return expiresInMinutes; }
    public void setExpiresInMinutes(long expiresInMinutes) { this.expiresInMinutes = expiresInMinutes; }

    public LocalDateTime getTimeCooked() { return timeCooked; }
    public void setTimeCooked(LocalDateTime timeCooked) { this.timeCooked = timeCooked; }
}
