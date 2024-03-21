package com.thuctap.bookmanage.convert;

import com.thuctap.bookmanage.dao.request.ListBookDTO;
import com.thuctap.bookmanage.dao.request.ListBookDTO.BookDTO;
import com.thuctap.bookmanage.dao.request.ListTypeDTO;
import com.thuctap.bookmanage.entity.ListBook;
import com.thuctap.bookmanage.entity.Type;
import com.thuctap.bookmanage.entity.Type_book;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookConverter {
  public ListBookDTO convert(List<ListBook> input) {
    ListBookDTO result = new ListBookDTO();
    List<ListBookDTO.BookDTO> list = new ArrayList<>();
    for (ListBook item : input) {
      ListBookDTO.BookDTO bookDTO = new BookDTO();
      BeanUtils.copyProperties(item,bookDTO);
      bookDTO.setId_user(item.getId_user());
      list.add(bookDTO);
    }
    result.setBookDTOList(list);
    return result;
  }

  public ListTypeDTO convertToType(List<Type> input) {
    ListTypeDTO result = new ListTypeDTO();
    List<ListTypeDTO.TypeDTO> list = new ArrayList<>();
    for (Type item : input) {
      ListTypeDTO.TypeDTO typeDTO = new ListTypeDTO.TypeDTO();
      BeanUtils.copyProperties(item,typeDTO);
      list.add(typeDTO);
    }
    result.setTypeDTOS(list);
    return result;
  }
}
