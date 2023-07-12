package com.Reboot.Minty.support.service;

import com.Reboot.Minty.member.constant.Role;
import com.Reboot.Minty.support.dto.FileDto;
import com.Reboot.Minty.support.dto.ReplyDto;
import com.Reboot.Minty.support.dto.UserSupportDto;
import com.Reboot.Minty.support.entity.File;
import com.Reboot.Minty.support.entity.ReplyEntity;
import com.Reboot.Minty.support.entity.UserSupport;
import com.Reboot.Minty.support.repository.FileRepository;
import com.Reboot.Minty.support.repository.PostsRepository;
import com.Reboot.Minty.support.repository.ReplyRepository;
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
public class PostsService {
    @Autowired
    PostsRepository postsRepository;
    @Autowired
    ReplyRepository replyRepository;
    @Autowired
    FileRepository fileRepository;

    public void userSavePosts(UserSupport userSupport) {
        // 파일 정보 저장
        for (File file : userSupport.getFiles())
            file.setUserSupport(userSupport);

        postsRepository.save(userSupport);
    }

    public void userSaveNotFilePosts(UserSupport userSupport) {
        postsRepository.save(userSupport);
    }


    public void adminSavePosts(ReplyEntity replyEntity){

        replyRepository.save(replyEntity);
    }

    public List<UserSupport> getAllUserSupport() {
        List<UserSupport> userSupportList = postsRepository.findAll();
        return userSupportList;
    }

    public UserSupport getUserSupport(Long id){
        return postsRepository.findById(id).orElseThrow(EntityExistsException::new);
    }

    public FileDto getFileById(Long fileId) {
        File fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid File ID"));

        return FileDto.builder()
                .id(fileEntity.getId())
                .origFilename(fileEntity.getOrigFilename())
                .filename(fileEntity.getFilename())
                .filePath(fileEntity.getFilePath())
                .build();
    }

    public UserSupportDto getPost(Long id) {
        Optional<UserSupport> userSupportOptional = postsRepository.findById(id);
        UserSupport userSupport = userSupportOptional.orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        UserSupportDto userSupportDto = UserSupportDto.builder()
                .title(userSupport.getTitle())
                .name(userSupport.getName())
                .content(userSupport.getContent())
                .nickname(userSupport.getNickname())
                .verifyReply(userSupport.getVerifyReply())
                .userId(userSupport.getUserId())
                .createdDate(userSupport.getCreatedDate())
                .modifiedDate(userSupport.getModifiedDate())
                .build();

        List<FileDto> fileDtos = new ArrayList<>();

        // 파일 정보 가져오기
        for (File file : userSupport.getFiles()) {
            FileDto fileDto = FileDto.builder()
                    .id(file.getId())
                    .origFilename(file.getOrigFilename())
                    .filename(file.getFilename())
                    .filePath(file.getFilePath())
                    .build();

            fileDtos.add(fileDto);
        }

        userSupportDto.setFiles(fileDtos);

        return userSupportDto;
    }

    public ReplyDto getReply(Long supportId) {
        Optional<ReplyEntity> replyEntityOptional = replyRepository.findByUserSupportId(supportId);

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

    public Page<UserSupport> getPostList(Pageable pageable, Role userRole, Long userId, String searchBy, String keyword) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (userRole == Role.ADMIN) {
            if (searchBy != null && searchBy.equals("이름")) {
                return postsRepository.findAllByNameContainingIgnoreCase(keyword, pageable);
            } else if (searchBy != null && searchBy.equals("닉네임")) {
                return postsRepository.findAllByNicknameContainingIgnoreCase(keyword, pageable);
            } else {
                return postsRepository.findAll(pageable);
            }
        } else {
            if (searchBy != null && searchBy.equals("이름")) {
                return postsRepository.findByUserIdAndNameContainingIgnoreCase(userId, keyword, pageable);
            } else if (searchBy != null && searchBy.equals("닉네임")) {
                return postsRepository.findByUserIdAndNicknameContainingIgnoreCase(userId, keyword, pageable);
            }
            return postsRepository.findByUserId(userId, pageable);
        }
    }

    public Page<UserSupport> getPostListWithVerifyReply(Pageable pageable, Role userRole, Long userId, String searchBy, String keyword, String verifyReply) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (userRole == Role.ADMIN) {
            if (searchBy != null && searchBy.equals("이름")) {
                return postsRepository.findAllByNameContainingIgnoreCaseAndVerifyReply(keyword, verifyReply, pageable);
            } else if (searchBy != null && searchBy.equals("닉네임")) {
                return postsRepository.findAllByNicknameContainingIgnoreCaseAndVerifyReply(keyword, verifyReply, pageable);
            } else {
                return postsRepository.findAllByVerifyReply(verifyReply, pageable);
            }
        } else {
            if (searchBy != null && searchBy.equals("이름")) {
                return postsRepository.findByUserIdAndNameContainingIgnoreCaseAndVerifyReply(userId, keyword, verifyReply, pageable);
            } else if (searchBy != null && searchBy.equals("닉네임")) {
                return postsRepository.findByUserIdAndNicknameContainingIgnoreCaseAndVerifyReply(userId, keyword, verifyReply, pageable);
            } else {
                return postsRepository.findByUserIdAndVerifyReply(userId, verifyReply, pageable);
            }
        }
    }


    public void updatePost(Long id, UserSupportDto userSupportDto) {
        UserSupport post = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        post.setTitle(userSupportDto.getTitle());
        post.setContent(userSupportDto.getContent());

        post.getFiles().clear();

        List<File> files = convertFileDtosToEntities(userSupportDto.getFiles());

        for (File file : files) {
            file.setUserSupport(post);
            post.addFile(file);
        }

        postsRepository.save(post);
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


    public void updateVerfyReply(Long id, UserSupportDto userSupportDto) {
        UserSupport userSupport = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        userSupport.setVerifyReply("처리완료");
        postsRepository.save(userSupport);
    }

    public void updateAdminPost(Long supportId , ReplyDto updateReplyDto){
        ReplyEntity reply = replyRepository.findByUserSupportId(supportId)
                .orElseThrow(() -> new IllegalArgumentException("Reply not found for support_Id: " + supportId));
        reply.setReplyTitle(updateReplyDto.getReplyTitle());
        reply.setReplyContent(updateReplyDto.getReplyContent());
        replyRepository.save(reply);
    }

    public void updateAdminVerfyReply(Long id, UserSupportDto userSupportDto) {
        UserSupport userSupport = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        userSupport.setVerifyReply("처리중");
        postsRepository.save(userSupport);
    }

    @Transactional
    public void deletePost(Long id){
        UserSupport userSupport = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("지원 게시물을 찾을 수 없습니다. id: " + id));

        replyRepository.deleteByUserSupportId(userSupport.getId());
        postsRepository.deleteById(id);
    }
    @Transactional
    public void deleteAdminPost(Long id){
        UserSupport userSupport = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("지원 게시물을 찾을 수 없습니다. id: " + id));

        replyRepository.deleteByUserSupportId(userSupport.getId());
    }
}
