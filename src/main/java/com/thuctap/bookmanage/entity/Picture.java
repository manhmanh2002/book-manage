package com.thuctap.bookmanage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "picture")
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "id_chapter",referencedColumnName = "id_chapter")
    private Chapter chapter;
    private String link;
    private LocalDate createByDay;
    private LocalTime createByTime;

    public Picture(Chapter chapter, String fileName, LocalDate date, LocalTime time) {
        this.chapter = chapter;
        this.link = fileName;
        this.createByDay = date;
        this.createByTime = time;
    }

    public String getLink(String id_book) {
        return "/data/" + id_book + "/" + this.chapter.getId_chapter() + "/" + this.link;
    }
}
