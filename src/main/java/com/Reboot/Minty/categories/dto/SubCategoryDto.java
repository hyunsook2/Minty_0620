package com.Reboot.Minty.categories.dto;

import com.Reboot.Minty.categories.entity.SubCategory;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class SubCategoryDto {
    private Long id;
    private String name;
    private TopCategoryDto topCategory;

    private static ModelMapper modelMapper = new ModelMapper();

    public static SubCategoryDto of(SubCategory subCategory){
        return modelMapper.map(subCategory,SubCategoryDto.class);
    }
}
