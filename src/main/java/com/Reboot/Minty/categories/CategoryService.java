package com.Reboot.Minty.categories;

import com.Reboot.Minty.categories.entity.SubCategory;
import com.Reboot.Minty.categories.entity.TopCategory;
import com.Reboot.Minty.categories.repository.SubCategoryRepository;
import com.Reboot.Minty.categories.repository.TopCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private TopCategoryRepository topCategoryRepository;
    @Autowired
    private SubCategoryRepository subCategoryRepository;

    public List<TopCategory> getTopCategoryList(){
        return topCategoryRepository.findAll();
    }


    public List<SubCategory> getSubCategoryList(){ return subCategoryRepository.findAll();}
}
