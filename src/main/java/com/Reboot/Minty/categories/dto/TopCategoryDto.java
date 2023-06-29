package com.Reboot.Minty.categories.dto;

import com.Reboot.Minty.categories.entity.TopCategory;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class TopCategoryDto {
    private Long id;
    private String name;

    private static ModelMapper modelMapper = new ModelMapper();

    public static TopCategoryDto of(TopCategory topCategory){
        return modelMapper.map(topCategory,TopCategoryDto.class);
    }
}
