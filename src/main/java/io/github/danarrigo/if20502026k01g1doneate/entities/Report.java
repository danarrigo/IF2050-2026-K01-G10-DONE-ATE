package io.github.danarrigo.if20502026k01g1doneate.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_report") 
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Integer reportId;

    private Integer totalRescuedFood; 
    private LocalDate generatedDate;
    @ManyToOne
    @JoinColumn(name = "donator_username") 
    private Donator donator;

    public Report() {
    }

    public Report(Integer totalRescuedFood, LocalDate generatedDate, Donator donator) {
        this.totalRescuedFood = totalRescuedFood;
        this.generatedDate = generatedDate;
        this.donator = donator;
    }

    public Integer getReportId() {
        return reportId;
    }

    public Integer getTotalRescuedFood() {
        return totalRescuedFood;
    }

    public void setTotalRescuedFood(Integer totalRescuedFood) {
        this.totalRescuedFood = totalRescuedFood;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }

    public Donator getDonator() {
        return donator;
    }

    public void setDonator(Donator donator) {
        this.donator = donator;
    }
}