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
    @ManyToOne
    @JoinColumn(name = "id_user",referencedColumnName = "id_user")
    private User user;
    @ManyToOne
    @JoinColumn(name = "id_book",referencedColumnName = "id_list")
    private ListBook book;
    private LocalDate day;
    private LocalTime time;
}
