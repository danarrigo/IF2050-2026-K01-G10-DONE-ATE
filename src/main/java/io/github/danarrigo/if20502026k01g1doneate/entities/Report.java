package io.github.danarrigo.if20502026k01g1doneate.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID reportId;

    private Integer totalRescued;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "donator_id")
    private Donator donator;

    // Constructor Kosong (Wajib untuk JPA)
    public Report() {}

    // Constructor yang kita pakai di Service
    public Report(Integer totalRescued, LocalDate date, Donator donator) {
        this.totalRescued = totalRescued;
        this.date = date;
        this.donator = donator;
    }

    // Getter dan Setter
    public UUID getReportId() { return reportId; }
    public void setReportId(UUID reportId) { this.reportId = reportId; }

    public Integer getTotalRescued() { return totalRescued; }
    public void setTotalRescued(Integer totalRescued) { this.totalRescued = totalRescued; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Donator getDonator() { return donator; }
    public void setDonator(Donator donator) { this.donator = donator; }
}