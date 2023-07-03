package com.Reboot.Minty.job.dto;

import com.Reboot.Minty.job.entity.Job;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class JobFormDto {

    @NotBlank(message = "제목은 필수 입니다.")
    private String title;
    @NotBlank(message = "내용은 필수 입니다.")
    private String content;

    @NotBlank(message = "주소 입력은 필수 입니다.")
    private String jobLocation;

    private String payType;

    @Positive(message = "양수만 입력해주세요.")
    @NotNull(message = "가격은 필수입니다.")
    private Integer payTotal;

    private static ModelMapper modelMapper =  new ModelMapper();

    public static Job toEntity(JobFormDto dto ){return modelMapper.map(dto,Job.class);}

    public void updateEntity(Job job){
        modelMapper.map(this, job);
    }
}
