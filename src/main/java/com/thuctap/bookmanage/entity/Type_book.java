package com.thuctap.bookmanage.entity;

import lombok.*;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "type_book")

public class Type_book {
    @Id
    @GeneratedValue(strategy  = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "id_book",referencedColumnName = "id_list")
    private ListBook book;

    @ManyToOne
    @JoinColumn(name = "id_type",referencedColumnName = "id")
    private Type type;

}
