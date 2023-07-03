package com.Reboot.Minty.job.service;

import com.Reboot.Minty.config.ResizeFile;
import com.Reboot.Minty.job.dto.JobDto;
import com.Reboot.Minty.job.dto.JobFormDto;
import com.Reboot.Minty.job.dto.JobSearchDto;
import com.Reboot.Minty.job.entity.Job;
import com.Reboot.Minty.job.entity.JobImage;

import com.Reboot.Minty.job.repository.JobCustomRepository;
import com.Reboot.Minty.job.repository.JobImageRepository;
import com.Reboot.Minty.job.repository.JobRepository;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobImageRepository jobImageRepository;
    private final UserRepository userRepository;
    private final Storage storage;

    private final JobCustomRepository jobCustomRepository;

    public JobService(JobRepository jobRepository, JobImageRepository jobImageRepository, UserRepository userRepository, Storage storage, JobCustomRepository jobCustomRepository){
        this.jobRepository=jobRepository;
        this.jobImageRepository = jobImageRepository;
        this.userRepository = userRepository;
        this.storage = storage;
        this.jobCustomRepository = jobCustomRepository;
    }

    public Page<Job> getAll(Pageable pageable){
        Page<Job> jobs = jobRepository.findAll(pageable);
        return jobs;
    }

    public Page<Job> getAllBySearchText(Optional<String> searchText, Pageable pageable){
        Page<Job> jobs = jobRepository.findAllByTitleContaining(searchText, pageable);
        return jobs;
    }

    public Page<JobDto> getJobPage(JobSearchDto jobSearchDto, Pageable pageable){
        return jobCustomRepository.findJobsBySearchDto(jobSearchDto, pageable);
    }



    @Value("${spring.cloud.gcp.storage.credentials.bucket}")
    private String bucketName;


    public Long save(Long userId, JobFormDto jobFormDto, List<MultipartFile> mf){
        String uuid = UUID.randomUUID().toString();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("로그인 된 유저만 가능합니다."));


        Job job = jobFormDto.toEntity(jobFormDto);
        job.setThumbnail(uuid);
        MultipartFile firstFile = mf.get(0);
        Job savedJob = jobRepository.save(job);
        Long savedJobId = savedJob.getId();
        try{
            MultipartFile resizedFirstFile = ResizeFile.resizeImage(firstFile, 360, 360);
            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder(bucketName, uuid)
                            .setContentType("image/jpg")
                            .build(),
                    resizedFirstFile.getInputStream()
            );
        }catch (IOException e){
            throw new RuntimeException("파일은 필수 입니다.");
        }
        try{
            for (int i = 0; i < mf.size(); i++) {
                uuid = UUID.randomUUID().toString();
                MultipartFile files = mf.get(i);
                String fileName = uuid;
                MultipartFile resizedFile = ResizeFile.resizeImage(files, 800, 600);
                BlobInfo blobInfo = storage.create(
                        BlobInfo.newBuilder(bucketName, uuid)
                                .setContentType("image/jpg")
                                .build(),
                        resizedFile.getInputStream()
                );
                JobImage jobImage = new JobImage();
                jobImage.setJob(savedJob);
                jobImage.setImgUrl(fileName);
                jobImageRepository.save(jobImage);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일은 필수 입니다.");
        }

        return savedJobId;
    }
}
