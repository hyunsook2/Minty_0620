package com.Reboot.Minty.job.repository;

import com.Reboot.Minty.job.dto.JobSearchDto;
import com.Reboot.Minty.job.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {

    Page<Job> findAllByTitleContaining(Optional<String> searchText, Pageable pageable);
}
