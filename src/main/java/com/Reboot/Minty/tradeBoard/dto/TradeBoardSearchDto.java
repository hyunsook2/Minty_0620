package com.Reboot.Minty.tradeBoard.dto;

import com.Reboot.Minty.categories.dto.SubCategoryDto;
import com.Reboot.Minty.categories.entity.SubCategory;
import lombok.Data;

@Data
public class TradeBoardSearchDto {
    Long subCategoryId;
    int minPrice;
    int maxPrice;
    String sortBy;
    String searchQuery="";
}
