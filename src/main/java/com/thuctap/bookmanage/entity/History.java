package com.thuctap.bookmanage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long id_user;
    private Long id_book;
    private LocalDate day;
    private LocalTime time;

    public History(Long id_user, Long id_book, LocalDate day, LocalTime time) {
        this.id_user = id_user;
        this.id_book = id_book;
        this.day = day;
        this.time = time;
    }
}
