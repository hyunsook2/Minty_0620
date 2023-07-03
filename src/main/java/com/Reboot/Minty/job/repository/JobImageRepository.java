package com.Reboot.Minty.job.repository;

import com.Reboot.Minty.job.entity.JobImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobImageRepository extends JpaRepository<JobImage,Long> {
}
