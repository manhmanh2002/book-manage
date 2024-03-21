package com.thuctap.bookmanage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_user;
    @NotBlank
    @Size(min=4,max=20,message = "Name should have at least 4 characters and no more than 20 characters!!!")
    private String name;
    @NotBlank
    @Size(min=6,max=20,message = "UserName should have at least 6 characters and no more than 20 characters!!!")
    private String password;
    private String new_pass;
    @Column(name = "token")
    private String token;
    private String gender;
    @NotBlank
    private String email;
    @NotBlank
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name= "photo",nullable = true,  length = 128)
    private String photo;
    @Enumerated(EnumType.STRING)
    private Enable enable;
    public String getPhoto() {
        if(photo == null)
            return "user-photos/default/user_default.png";
        return "/user-photos/" + getId_user() + "/" + photo;
    }


}
