package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
import io.github.danarrigo.if20502026k01g1doneate.services.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.danarrigo.if20502026k01g1doneate.dtos.QCFormData;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping("/submit-qc-form")
    /*
     * "Apakah hidangan berbau segar dan normal?"
     * "Apakah makanan bebas dari tanda-tanda basi atau rusak?"
     * "Apakah hidangan dimasak/disiapkan dengan benar?"
     * "Apakah ada bahan kedaluwarsa yang digunakan?"
     * "Apakah hidangan bebas dari benda asing (rambut, plastik, serangga, dll.)?"
     * "Apakah makanan disimpan pada suhu yang aman?"
     * "Apakah warna hidangan terlihat normal dan layak?"
     * "Apakah tekstur makanan masih layak untuk dikonsumsi?"
     * "Apakah hidangan aman untuk dimakan?"
     * "Apakah hidangan layak disajikan kepada pelanggan?"
     * 
     * 
     */
    public ResponseEntity<Void> submitQCFormData(@RequestBody QCFormData body) {
        dishService.dishQualityControl(body);
        return ResponseEntity.noContent().build();
    }

    // GET
    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes() {
        List<Dish> dishes = dishService.getAllDishes();
        return ResponseEntity.ok(dishes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable UUID id) {
        Dish dish = dishService.getDishById(id);
        return ResponseEntity.ok(dish);
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable UUID id, @RequestBody Dish newDish) {
        Dish dish = dishService.updateDish(newDish, id);
        return ResponseEntity.ok(dish);
    }

    // POST
    @PostMapping
    public ResponseEntity<Dish> createDish(@RequestBody Dish dish) {
        Dish addedDish = dishService.createDish(dish);
        return ResponseEntity.ok(addedDish);
    }

    // DELETE
    @DeleteMapping
    public ResponseEntity<Void> deleteAllDishes() {
        dishService.deleteDishes();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDishById(@PathVariable UUID id) {
        dishService.deleteDishById(id);
        return ResponseEntity.noContent().build();
    }
}
