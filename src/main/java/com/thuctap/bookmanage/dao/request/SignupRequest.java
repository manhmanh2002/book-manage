package com.thuctap.bookmanage.dao.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(min=4,max=20,message = "Name should have at least 4 characters and no more than 20 characters!!!")
    private String name;
    @NotBlank
    @Size(min=4,max=20,message = "Password should have at least 6 characters and no more than 20 characters!!!")
    private String password;
    @NotBlank
    private String gender;
    @NotBlank
    private String email;

    private String photo;

}
