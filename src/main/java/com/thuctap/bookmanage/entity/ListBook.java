package com.thuctap.bookmanage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
@NoArgsConstructor
@Data
@Entity
@Table(name = "listbook")
public class ListBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_list;
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user",referencedColumnName = "id_user")
    @JsonIgnore(value = false)
    private User user;
    private String name_book;
    @Column(length = Integer.MAX_VALUE)
    private String description;
    private int count_chapter;
    private int count_view;
    private String image;
    private Date day;


    public ListBook(User user, String name, int count_chapter, String image, String content, Date day) {
        this.user = user;
        this.name_book = name;
        this.count_chapter=count_chapter;
        this.image = image;
        this.description = content;
        this.day = day;
    }
    public String getImage() {
        return "/data/" + getId_list() + "/" + image;
    }

    public Long getId_user() {
        return user.getId_user();
    }
}
