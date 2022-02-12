package com.imjustdoom.pluginsite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Resource {

    public Resource(String name, String description, String blurb, String donation, String source, String download,
                    Account author, String support, String category) {
        this.name = name;
        this.description = description;
        this.blurb = blurb;
        this.donation = donation;
        this.source = source;
        this.download = download;
        this.author = author;
        this.support = support;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
        this.category = category;
        this.status = "public";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String blurb;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime updated;

    // todo if we are making these option, they should also be nullable??
    @Column(nullable = false)
    private String donation;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String download;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account author;

    @Column(nullable = false)
    private String support;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String status;

    @Lob
    private byte[] logo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resource")
    private List<Update> updates;
}