package com.imjustdoom.pluginsite.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "update_table")
public class Update {

    public Update(String description, String filename, String version, String download, String name,
                  List<String> versions, List<String> software, Resource resource, String external) {
        this.downloads = 0;
        this.description = description;
        this.filename = filename;
        this.version = version;
        this.download = download;
        this.name = name;
        this.uploaded = LocalDateTime.now();
        this.versions = versions;
        this.software = software;
        this.resource = resource;
        this.external = external;
        this.status = "public";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String filename;

    @ElementCollection
    private List<String> versions;

    @ElementCollection
    private List<String> software;

    @Column(nullable = false)
    private String download;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime uploaded;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private int downloads;

    @Column(nullable = false)
    private String external;

    @ManyToOne(fetch = FetchType.LAZY)
    private Resource resource;

    @Column(nullable = false)
    private String status;

    public Update() {

    }
}
