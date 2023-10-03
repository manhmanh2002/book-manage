package com.thuctap.bookmanage.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chapter")

public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator="native")
    private Long id_chapter;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "id_list",referencedColumnName = "id_list")
    private ListBook book;
    private int number;
    private String content;
    private int count_chapter;
    private Long preChap;
    private Long nextChap;
    private LocalDate day;
    private LocalTime time;

    public Chapter(ListBook book, int number, String content, int countchapter,Long prechap ,Long nextchap, LocalDate daycreate, LocalTime timecreate) {
        this.book= book;
        this.number=number;
        this.content=content;
        this.count_chapter = countchapter;
        this.day = daycreate;
        this.time = timecreate;
        this.preChap=prechap;
        this.nextChap=nextchap;
    }
}
