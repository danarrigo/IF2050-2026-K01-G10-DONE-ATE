package entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="dishes")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID dishId ;
    private String name;
    private String imagePath;

    public Dish(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }

    public Dish() {

    }

    public String getDetails(){
        return "Dish ID: " + dishId + "\n" +
                "Name: " + name + "\n" +
                "Image Path: " + imagePath;
    }

    public UUID getDishId() {
        return dishId;
    }

    public void setDishId(UUID dishId) {
        this.dishId = dishId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
