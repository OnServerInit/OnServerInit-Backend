package com.imjustdoom.pluginsite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class MessageGroup {

    public MessageGroup(String name, LocalDateTime createdTime, List<Account> members) {
        this.name = name;
        this.createdTime = createdTime;
        this.members = members;
        System.out.println(members.toString());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable
    private List<Account> members;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "group")
    private List<Message> messages;
}
