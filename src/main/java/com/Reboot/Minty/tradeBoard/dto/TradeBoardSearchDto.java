package com.Reboot.Minty.tradeBoard.dto;

import com.Reboot.Minty.categories.dto.SubCategoryDto;
import com.Reboot.Minty.categories.entity.SubCategory;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TradeBoardSearchDto {
    Long subCategoryId;
    int minPrice;
    int maxPrice;
    String sortBy;
    String searchQuery="";
    List<String> searchArea = new ArrayList<>();
}
