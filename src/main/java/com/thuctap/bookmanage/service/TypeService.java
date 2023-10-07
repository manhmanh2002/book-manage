package com.thuctap.bookmanage.service;

import com.thuctap.bookmanage.entity.Type;
import com.thuctap.bookmanage.repository.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TypeService {
    @Autowired
    private TypeRepository typeRepository;

    public void saveType(){
        String[] typeName = {"Action" , "Adult" , "Adventure'" , "Anime" , "Isekai" , "Comedy" , "Comic" , "Demons" , "Detective" , "Doujishi" , "Fantasy" , "Harem" , "Magic" , "School life" , "Shounen"};
        for(String i : typeName){
            Type type = new Type();
            type.setType_name(i);
            System.out.println(type);
            typeRepository.save(type);
        }

    }
    public Type findbyID(Long idtype){
        return typeRepository.findByID(idtype);
    }

}
