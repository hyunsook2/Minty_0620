package com.Reboot.Minty.categories;

import com.Reboot.Minty.categories.dto.SubCategoryDto;
import com.Reboot.Minty.categories.dto.TopCategoryDto;
import com.Reboot.Minty.categories.entity.SubCategory;
import com.Reboot.Minty.categories.entity.TopCategory;
import com.Reboot.Minty.categories.repository.SubCategoryRepository;
import com.Reboot.Minty.categories.repository.TopCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private TopCategoryRepository topCategoryRepository;
    @Autowired
    private SubCategoryRepository subCategoryRepository;

    public List<TopCategoryDto> getTopCategoryList(){
        List<TopCategory> entity =topCategoryRepository.findAll();
        List<TopCategoryDto> dto = entity.stream().map(TopCategoryDto::of).collect(Collectors.toList());
        return dto;
    }


    public List<SubCategoryDto> getSubCategoryList(){
        List<SubCategory> entity = subCategoryRepository.findAll();
        List<SubCategoryDto> dto = entity.stream().map(SubCategoryDto::of).collect(Collectors.toList());
        return dto;
    }
}
