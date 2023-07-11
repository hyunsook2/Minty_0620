package com.Reboot.Minty.support.controller;

import com.Reboot.Minty.member.constant.Role;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.support.dto.FileDto;
import com.Reboot.Minty.support.dto.ReplyDto;
import com.Reboot.Minty.support.dto.ReportDto;
import com.Reboot.Minty.support.entity.File;
import com.Reboot.Minty.support.entity.ReplyEntity;
import com.Reboot.Minty.support.entity.Report;
import com.Reboot.Minty.support.entity.UserSupport;
import com.Reboot.Minty.support.repository.ReportRepository;
import com.Reboot.Minty.support.service.FileService;
import com.Reboot.Minty.support.service.PostsService;
import com.Reboot.Minty.support.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Controller
public class ReportController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostsService postsService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private FileDtoConverter fileDtoConverter;

    //신고게시판 이동
    @GetMapping("/reportBoard")
    public String reportList(Model model, @PageableDefault(size =10) Pageable pageable,
                             HttpServletRequest request, @RequestParam(required = false) String searchBy,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String verifyReply){
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        Role userRole = user.getRole();
        Long userId = user.getId();
        Page<Report> reportList;

        if (verifyReply != null && !verifyReply.isEmpty()) {
            reportList = reportService.getReportListWithVerifyReply(pageable, userRole, userId, searchBy, keyword, verifyReply);
        } else {
            reportList = reportService.getReportList(pageable, userRole, userId, searchBy, keyword);
        }

        model.addAttribute("reportList", reportList);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("keyword", keyword);
        model.addAttribute("verifyReply", verifyReply);

        return "report/reportBoard";
    }

    //신고 게시물 보기
    @GetMapping("/reportView/{id}")
    public String reportView(@PathVariable("id") Long id, Model model){
        ReportDto reportDto = reportService.getReport(id);
        ReplyDto replyDto = reportService.getReply(id);

        model.addAttribute("replyDto", replyDto);
        model.addAttribute("reportDto", reportDto);
        model.addAttribute("id", id);

        return "report/reportView";
    }

    //유저가 신고글 작성할때 가져오는 정보
    @GetMapping(value ="/report/new")
    public String reportForm(ReportDto reportDto, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        reportDto.setNickname(user.getNickName());
        reportDto.setName(user.getName());
        reportDto.setUserId(user.getId());
        model.addAttribute("reportDto", reportDto);
        return "report/reportPage";
    }

    //유저가 신고글 포스팅할때
    @PostMapping(value = "/report/new")
    public String reportSaveForm(@RequestParam("files") List<MultipartFile> files, @ModelAttribute @Valid ReportDto reportDto
                                , HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            String userEmail = (String) session.getAttribute("userEmail");
            User user = userService.getUserInfo(userEmail);
            reportDto.setNickname(user.getNickName());
            reportDto.setName(user.getName());
            reportDto.setUserId(user.getId());
            reportDto.setVerifyReply("처리중");

            List<String> filePaths = fileService.saveFiles(files);

            if (!filePaths.isEmpty()) {
                List<FileDto> fileDtos = new ArrayList<>();
                for (String filePath : filePaths) {
                    FileDto fileDto = new FileDto();
                    fileDto.setOrigFilename(getOriginalFilenameFromPath(filePath));
                    fileDto.setFilename(getFilenameFromPath(filePath));
                    fileDto.setFilePath(filePath);
                    fileDtos.add(fileDto);
                }

                reportDto.setFiles(fileDtos);

                Report report = reportDto.toEntity();
                report.setFiles(new ArrayList<>());

                // 파일 정보 저장
                for (FileDto fileDto : fileDtos) {
                    File file = fileDto.toEntity();
                    file.setReport(report);
                    report.addFile(file);
                }

                reportService.reportSave(report);
            }else{
                Report report = reportDto.toEntity();
                reportService.reportSaveNotFile(report);
                return "redirect:/reportBoard";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/reportBoard";
    }

    private String getOriginalFilenameFromPath(String filePath) {
        if (filePath == null) {
            return "";
        }
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
    }

    private String getFilenameFromPath(String filePath) {
        if (filePath == null) {
            return "";
        }
        Path path = Paths.get(filePath);
        if (Files.exists(path) && !Files.isDirectory(path)) {
            return path.getFileName().toString();
        } else {
            return "";
        }
    }

    //관리자가 답변글 작성할때 가져오는 정보
    @GetMapping(value = "/report/new/admin/{id}")
    public String reportAdminForm(@PathVariable("id") Long id,ReplyDto replyDto, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        replyDto.setNickname(user.getNickName());
        model.addAttribute("id",id);
        model.addAttribute("replyDto", replyDto);
        return "report/reportAdminPage";
    }

    //관리자가 답변글 포스팅할때
    @PostMapping(value = "/report/new/admin")
    public String reportAdminSaveForm(  @RequestParam("id") Long id,
                                         ReplyDto replyDto,
                                         HttpServletRequest request,
                                         ReportDto reportDto){

        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        replyDto.setNickname(user.getNickName());
        ReplyEntity replyEntity = replyDto.toEntity();
        Report report = reportService.getReportById(id);

        replyEntity.setReport(report);
        postsService.adminSavePosts(replyEntity);

        if (report.getVerifyReply().equals("처리중")) {
            reportService.updateVerifyReply(id, reportDto);
        }

        return "redirect:/reportBoard";
    }

    //게시글 수정
    // 신고게시물 수정 페이지
    @GetMapping("/report/edit/{id}")
    public String reportedit(@PathVariable("id") Long id, Model model) {
        ReportDto reportDto = reportService.getReport(id);
        model.addAttribute("id",id);
        model.addAttribute("reportDto", reportDto);
        return "report/reportEdit";
    }

    // 게시글 수정 처리
    @PostMapping("/report/edit/{id}")
    public String updatePost(
            @PathVariable("id") Long id,
            @ModelAttribute("reportDto") @Valid ReportDto reportDto,
            BindingResult bindingResult,
            @RequestParam("files") List<MultipartFile> files
    ) {
        if (bindingResult.hasErrors()) {
            return "edit-support";
        }

        List<FileDto> fileDtos = convertMultipartFilesToDtos(files);
        reportDto.setFiles(fileDtos);

        reportService.updateReport(id, reportDto);

        return "redirect:/reportView/{id}";
    }

    private List<FileDto> convertMultipartFilesToDtos(List<MultipartFile> multipartFiles) {
        List<FileDto> fileDtos = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            FileDto fileDto = fileDtoConverter.convert(multipartFile);
            fileDtos.add(fileDto);
        }
        return fileDtos;
    }

    //신고게시물 삭제
    @GetMapping("/report/delete/{id}")
    public String deleteReportPost(@PathVariable("id") Long id) {
        reportService.deleteReport(id);
        return "redirect:/reportBoard";
    }

    // 관리자 답글 수정
    @GetMapping("/report/edit/admin/{id}")
    public String editReportAdmin(@PathVariable("id") Long id, Model model) {
        ReplyDto replyDto = reportService.getReply(id);
        model.addAttribute("id",id);
        model.addAttribute("replyDto", replyDto);
        return "report/reportAdminEdit";
    }

    // 관리자 답글 수정처리
    @PostMapping("/report/edit/admin/{id}")
    public String updateAdmin(@PathVariable("id") Long id, ReplyDto replyDto) {
        reportService.updateAdminReport(id, replyDto);
        return "redirect:/reportView/{id}";
    }

    //관리자 답글 삭제
    @GetMapping("/report/delete/admin/{id}")
    public String deleteAdminPost(@PathVariable("id") Long id, ReportDto reportDto) {
        UserSupport userSupport = postsService.getUserSupport(id);
        Report report = reportService.getReportById(id);
        if (report.getVerifyReply().equals("처리완료")) {
            reportService.updateAdminVrfyReply(id);
        }
        reportService.deleteAdminReport(id);
        return "redirect:/reportBoard";
    }
}
