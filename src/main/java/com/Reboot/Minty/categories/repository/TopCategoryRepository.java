package com.Reboot.Minty.categories.repository;

import com.Reboot.Minty.categories.entity.TopCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopCategoryRepository extends JpaRepository<TopCategory,Long> {
}
