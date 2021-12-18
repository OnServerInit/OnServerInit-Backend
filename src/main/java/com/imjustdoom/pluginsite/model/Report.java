package com.imjustdoom.pluginsite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "reports")
public class Report {

    public Report(Account reporter, String reportingObject, String reportingId, String report, String reason) {
        this.reporter = reporter;
        this.reportingObject = reportingObject;
        this.reportingId = reportingId;
        this.report = report;
        this.reason = reason;
        this.reportedDate = LocalDateTime.now();
        this.actionTaken = "none";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account reporter;

    @Column(nullable = false)
    private String reportingObject;

    @Column(nullable = false)
    private String reportingId;

    @Column(nullable = false)
    private String report;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime reportedDate;

    @Column(nullable = false)
    private String actionTaken;
}