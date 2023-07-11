package com.Reboot.Minty.support.controller;

import com.Reboot.Minty.member.constant.Role;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.support.dto.FileDto;
import com.Reboot.Minty.support.dto.ReplyDto;
import com.Reboot.Minty.support.dto.UserSupportDto;
import com.Reboot.Minty.support.entity.File;
import com.Reboot.Minty.support.entity.ReplyEntity;
import com.Reboot.Minty.support.entity.UserSupport;
import com.Reboot.Minty.support.repository.PostsRepository;
import com.Reboot.Minty.support.service.FileService;
import com.Reboot.Minty.support.service.PostsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
@Controller
public class SupportController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostsService postsService;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private FileDtoConverter fileDtoConverter;


    //일반문의 게시판 이동
    @GetMapping("/supportBoard")
    public String postList(Model model, @PageableDefault(size = 10) Pageable pageable,
                           HttpServletRequest request, @RequestParam(required = false) String searchBy,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String verifyReply) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        Role userRole = user.getRole();
        Long userId = user.getId();
        Page<UserSupport> postList;

        if (verifyReply != null && !verifyReply.isEmpty()) {
            postList = postsService.getPostListWithVerifyReply(pageable, userRole, userId, searchBy, keyword, verifyReply);
        } else {
            postList = postsService.getPostList(pageable, userRole, userId, searchBy, keyword);
        }

        model.addAttribute("postList", postList);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("keyword", keyword);
        model.addAttribute("verifyReply", verifyReply);

        return "support/supportBoard";
    }

    // 문의 게시물 보기
    @GetMapping("/supportView/{id}")
    public String supportView(@PathVariable("id") Long id, Model model) {

        UserSupportDto postsDto = postsService.getPost(id);
        ReplyDto replyDto = postsService.getReply(id);

        model.addAttribute("replyDto", replyDto);
        model.addAttribute("postDto", postsDto);
        model.addAttribute("id", id);

        return "support/supportView";
    }

    //다운로드
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") Long fileId) throws IOException {
        FileDto fileDto = postsService.getFileById(fileId);

        if (fileDto == null || fileDto.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        Resource fileResource = fileService.getFile(fileDto.getFilePath());

        String encodedFileName = URLEncoder.encode(fileDto.getOrigFilename(), StandardCharsets.UTF_8.toString());
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileResource);
    }

    //유저가 문의글 작성할때 가져오는 정보
    @GetMapping(value = "/new")
    public String supportForm(UserSupportDto userSupportDto, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        userSupportDto.setNickname(user.getNickName());
        userSupportDto.setName(user.getName());
        userSupportDto.setUserId(user.getId());
        model.addAttribute("userSupportDto", userSupportDto);
        return "support/supportPage";
    }

    //유저가 문의글 포스팅할때
    @PostMapping(value = "/new")
    public String supportSaveForm(@RequestParam("files") List<MultipartFile> files, @ModelAttribute @Valid UserSupportDto userSupportDto, BindingResult bindingResult, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            String userEmail = (String) session.getAttribute("userEmail");
            User user = userService.getUserInfo(userEmail);
            userSupportDto.setNickname(user.getNickName());
            userSupportDto.setName(user.getName());
            userSupportDto.setUserId(user.getId());
            userSupportDto.setVerifyReply("처리중");

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

                userSupportDto.setFiles(fileDtos);

                UserSupport userSupport = userSupportDto.toEntity();
                userSupport.setFiles(new ArrayList<>());

                // 파일 정보 저장
                for (FileDto fileDto : fileDtos) {
                    File file = fileDto.toEntity();
                    file.setUserSupport(userSupport);
                    userSupport.addFile(file);
                }

                postsService.userSavePosts(userSupport);
            }else{
                UserSupport userSupport = userSupportDto.toEntity();
                postsService.userSaveNotFilePosts(userSupport);
                return "redirect:/supportBoard";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/supportBoard";
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
    @GetMapping(value = "/new/admin/{id}")
    public String supportAdminForm(@PathVariable("id") Long id,ReplyDto replyDto, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        replyDto.setNickname(user.getNickName());
        model.addAttribute("id",id);
        model.addAttribute("replyDto", replyDto);
        return "support/supportAdminPage";
    }

    //관리자가 답변글 포스팅할때
    @PostMapping(value = "/new/admin")
    public String supportAdminSaveForm(  @RequestParam("id") Long id,
                                          ReplyDto replyDto,
                                          HttpServletRequest request,
                                         UserSupportDto userSupportDto){

        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        replyDto.setNickname(user.getNickName());
        ReplyEntity replyEntity = replyDto.toEntity();
        UserSupport userSupport = postsService.getUserSupport(id);

        replyEntity.setUserSupport(userSupport);
        postsService.adminSavePosts(replyEntity);

        if (userSupport.getVerifyReply().equals("처리중")) {
            postsService.updateVerfyReply(id, userSupportDto);
        }

        return "redirect:/supportBoard";
    }

    //게시글 수정
    // 게시글 수정 페이지
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        UserSupportDto postDto = postsService.getPost(id);
        model.addAttribute("id",id);
        model.addAttribute("postDto", postDto);
        return "support/supportEdit";
    }

    // 게시글 수정 처리
    @PostMapping("/edit/{id}")
    public String updatePost(
            @PathVariable("id") Long id,
            @ModelAttribute("postDto") @Valid UserSupportDto userSupportDto,
            BindingResult bindingResult,
            @RequestParam("files") List<MultipartFile> files
    ) {
        if (bindingResult.hasErrors()) {
            return "edit-support";
        }

        List<FileDto> fileDtos = convertMultipartFilesToDtos(files);
        userSupportDto.setFiles(fileDtos);

        postsService.updatePost(id, userSupportDto);

        return "redirect:/supportView/{id}";
    }

    private List<FileDto> convertMultipartFilesToDtos(List<MultipartFile> multipartFiles) {
        List<FileDto> fileDtos = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            FileDto fileDto = fileDtoConverter.convert(multipartFile);
            fileDtos.add(fileDto);
        }
        return fileDtos;
    }


    //게시물 삭제
    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        postsService.deletePost(id);
        return "redirect:/supportBoard";
    }

    // 관리자 답글 수정
    @GetMapping("/edit/admin/{id}")
    public String editAdmin(@PathVariable("id") Long id, Model model) {
        ReplyDto replyDto = postsService.getReply(id);
        model.addAttribute("id",id);
        model.addAttribute("replyDto", replyDto);
        return "support/supportAdminEdit";
    }

    // 관리자 답글 수정처리
    @PostMapping("/edit/admin/{id}")
    public String updateAdmin(@PathVariable("id") Long id, ReplyDto replyDto) {
        postsService.updateAdminPost(id, replyDto);
        return "redirect:/supportView/{id}";
    }

    //관리자 답글 삭제
    @GetMapping("/delete/admin/{id}")
    public String deleteAdminPost(@PathVariable("id") Long id, UserSupportDto userSupportDto) {
        UserSupport userSupport = postsService.getUserSupport(id);
        if (userSupport.getVerifyReply().equals("처리완료")) {
            postsService.updateAdminVerfyReply(id, userSupportDto);
        }
        postsService.deleteAdminPost(id);
        return "redirect:/supportBoard";
    }

}
