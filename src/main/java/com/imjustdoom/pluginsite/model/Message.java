package com.imjustdoom.pluginsite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Id;

import javax.persistence.*;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Message {

    public Message(String content, Account author, MessageGroup group){
        this.content = content;
        this.author = author;
        this.group = group;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Account author;

    @ManyToOne(fetch = FetchType.LAZY)
    private MessageGroup group;

    @Column(nullable = false)
    private String content;
}
