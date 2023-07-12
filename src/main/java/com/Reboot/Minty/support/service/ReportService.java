package com.Reboot.Minty.support.service;

import com.Reboot.Minty.member.constant.Role;
import com.Reboot.Minty.support.dto.FileDto;
import com.Reboot.Minty.support.dto.ReplyDto;
import com.Reboot.Minty.support.dto.ReportDto;
import com.Reboot.Minty.support.entity.File;
import com.Reboot.Minty.support.entity.ReplyEntity;
import com.Reboot.Minty.support.entity.Report;
import com.Reboot.Minty.support.repository.FileRepository;
import com.Reboot.Minty.support.repository.ReplyRepository;
import com.Reboot.Minty.support.repository.ReportRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    ReplyRepository replyRepository;

    //신고 문의 저장(파일 존재)
    public void reportSave(Report report){
        for(File file : report.getFiles())
            file.setReport(report);

        reportRepository.save(report);
    }

    //신고 문의 저장(파일 없음)
    public void reportSaveNotFile(Report report){
        reportRepository.save(report);
    }

    public Report getReportById(Long id){
        return reportRepository.findById(id).orElseThrow(EntityExistsException::new);
    }

    //신고 문의 get
    public ReportDto getReport(Long id){
        Optional<Report> reportOptional = reportRepository.findById(id);
        Report report = reportOptional.orElseThrow(()->new IllegalArgumentException());

        ReportDto reportDto = ReportDto.builder()
                .title(report.getTitle())
                .name(report.getName())
                .content(report.getContent())
                .nickname(report.getNickname())
                .reportNickname(report.getReportNickname())
                .verifyReply(report.getVerifyReply())
                .userId(report.getUserId())
                .createdDate(report.getCreatedDate())
                .modifiedDate(report.getModifiedDate())
                .build();

        List<FileDto> fileDtos = new ArrayList<>();

        // 파일 정보 가져오기
        for (File file : report.getFiles()) {
            FileDto fileDto = FileDto.builder()
                    .id(file.getId())
                    .origFilename(file.getOrigFilename())
                    .filename(file.getFilename())
                    .filePath(file.getFilePath())
                    .build();

            fileDtos.add(fileDto);
        }

        reportDto.setFiles(fileDtos);

        return reportDto;
    }

    //신고 답글 get
    public ReplyDto getReply(Long reportId) {
        Optional<ReplyEntity> replyEntityOptional = replyRepository.findByReportId(reportId);

        if (replyEntityOptional.isPresent()) {
            ReplyEntity replyEntity = replyEntityOptional.get();
            ReplyDto replyDto = ReplyDto.builder()
                    .replyTitle(replyEntity.getReplyTitle())
                    .replyContent(replyEntity.getReplyContent())
                    .nickname(replyEntity.getNickname())
                    .createdDate(replyEntity.getCreatedDate())
                    .modifiedDate(replyEntity.getModifiedDate())
                    .build();
            return replyDto;
        } else {
            return null;
        }
    }

    //처리상태 포함 x
    public Page<Report> getReportList(Pageable pageable, Role userRole, Long userId, String searchBy, String keyword){
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (userRole == Role.ADMIN) {
            if (searchBy != null && searchBy.equals("이름")) {
                return reportRepository.findAllByNameContainingIgnoreCase(keyword, pageable);
            } else if (searchBy != null && searchBy.equals("닉네임")) {
                return reportRepository.findAllByNicknameContainingIgnoreCase(keyword, pageable);
            } else {
                return reportRepository.findAll(pageable);
            }
        } else {
            if (searchBy != null && searchBy.equals("이름")) {
                return reportRepository.findByUserIdAndNameContainingIgnoreCase(userId, keyword, pageable);
            } else if (searchBy != null && searchBy.equals("닉네임")) {
                return reportRepository.findByUserIdAndNicknameContainingIgnoreCase(userId, keyword, pageable);
            }
            return reportRepository.findByUserId(userId, pageable);
        }
    }

    public Page<Report> getReportListWithVerifyReply(Pageable pageable, Role userRole, Long userId, String searchBy, String keyword, String verifyReply) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (userRole == Role.ADMIN) {
            if (searchBy != null && searchBy.equals("이름")) {
                return reportRepository.findAllByNameContainingIgnoreCaseAndVerifyReply(keyword, verifyReply, pageable);
            } else if (searchBy != null && searchBy.equals("닉네임")) {
                return reportRepository.findAllByNicknameContainingIgnoreCaseAndVerifyReply(keyword, verifyReply, pageable);
            } else {
                return reportRepository.findAllByVerifyReply(verifyReply, pageable);
            }
        } else {
            if (searchBy != null && searchBy.equals("이름")) {
                return reportRepository.findByUserIdAndNameContainingIgnoreCaseAndVerifyReply(userId, keyword, verifyReply, pageable);
            } else if (searchBy != null && searchBy.equals("닉네임")) {
                return reportRepository.findByUserIdAndNicknameContainingIgnoreCaseAndVerifyReply(userId, keyword, verifyReply, pageable);
            } else {
                return reportRepository.findByUserIdAndVerifyReply(userId, verifyReply, pageable);
            }
        }
    }

    //신고 문의 업데이트
    public void updateReport(Long id, ReportDto reportDto){
        Report report = reportRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Invalid post ID"));

        report.setTitle(reportDto.getTitle());
        report.setContent(reportDto.getContent());

        report.getFiles().clear();

        List<File> files = convertFileDtosToEntities(reportDto.getFiles());

        for(File file : files){
            file.setReport(report);
            report.addFile(file);
        }

        reportRepository.save(report);
    }

    private List<File> convertFileDtosToEntities(List<FileDto> fileDtos) {
        List<File> files = new ArrayList<>();
        if (fileDtos != null) {
            for (FileDto fileDto : fileDtos) {
                if (fileDto != null) {
                    File file = fileDto.toEntity();
                    files.add(file);
                }
            }
        }
        return files;
    }

    //답변 등록시 처리완료로 바뀜
    public void updateVerifyReply(Long id, ReportDto reportDto){
        Report report = reportRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Invalid post ID"));
        report.setVerifyReply("처리완료");
        reportRepository.save(report);
    }

    public void updateAdminReport(Long reportId, ReplyDto updateReplyDto){
        ReplyEntity reply = replyRepository.findByReportId(reportId).orElseThrow(()-> new IllegalArgumentException("Reply not found for report_Id"));
        reply.setReplyTitle(updateReplyDto.getReplyTitle());
        reply.setReplyContent(updateReplyDto.getReplyContent());
        replyRepository.save(reply);
    }

    public void updateAdminVrfyReply(Long id){
        Report report = reportRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Invalid ID"));
        report.setVerifyReply("처리중");
        reportRepository.save(report);
    }

    @Transactional
    public void deleteReport(Long id ){
        Report report = reportRepository.findById(id).orElseThrow(()->new IllegalArgumentException("신고 게시물을 찾을수 없습니다."));

        replyRepository.deleteByReportId(report.getId());
        reportRepository.deleteById(id);
    }

    @Transactional
    public void deleteAdminReport(Long id){
        Report report = reportRepository.findById(id).orElseThrow(()->new IllegalArgumentException("신고 게시물을 찾을 수 없습니다"));

        replyRepository.deleteByReportId(report.getId());
    }
}
