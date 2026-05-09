package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
import io.github.danarrigo.if20502026k01g1doneate.services.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }


    //GET
    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes(){
        List<Dish> dishes = dishService.getAllDishes();
        return ResponseEntity.ok(dishes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable UUID id){
        Dish dish = dishService.getDishById(id);
        return ResponseEntity.ok(dish);
    }

    //PUT
    @PutMapping ("/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable UUID id, @RequestBody Dish newDish){
        Dish dish = dishService.updateDish(newDish,id);
        return ResponseEntity.ok(dish);
    }

    //POST
    @PostMapping
    public ResponseEntity<Dish> createDish(@RequestBody Dish dish){
        Dish addedDish = dishService.createDish(dish);
        return ResponseEntity.ok(addedDish);
    }

    //DELETE
    @DeleteMapping
    public ResponseEntity<Void> deleteAllDishes(){
        dishService.deleteDishes();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDishById(@PathVariable UUID id){
        dishService.deleteDishById(id);
        return ResponseEntity.noContent().build();
    }
}
