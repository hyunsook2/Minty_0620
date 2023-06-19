package com.Reboot.Minty.tradeBoard.controller;

import com.Reboot.Minty.categories.CategoryService;
import com.Reboot.Minty.categories.entity.SubCategory;
import com.Reboot.Minty.categories.entity.TopCategory;
import com.Reboot.Minty.tradeBoard.dto.TradeBoardDto;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import com.Reboot.Minty.tradeBoard.entity.TradeBoardImg;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardRepository;
import com.Reboot.Minty.tradeBoard.service.TradeBoardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TradeBoardController {
    private final CategoryService categoryService;
    private final TradeBoardService tradeBoardService;
    private final TradeBoardRepository tradeBoardRepository;

    @Autowired
    public TradeBoardController(CategoryService categoryService, TradeBoardService tradeBoardService, TradeBoardRepository tradeBoardRepository) {
        this.categoryService = categoryService;
        this.tradeBoardService = tradeBoardService;
        this.tradeBoardRepository = tradeBoardRepository;
    }


    @GetMapping("/boardList/**")
    public String boardList(Model model) {
        return "../static/index";
    }
    //커밋용

    @GetMapping(value = {"/api/boardList/{boardType}/category/{category}/{page}", "/api/boardList/{boardType}/{page}", "/api/boardList/{boardType}"})
    @ResponseBody
    public Map<String, Object> getBoardList(
            @PathVariable("boardType") int boardType,
            @PathVariable(value = "category", required = false) Optional<SubCategory> category,
            @PathVariable(value = "page", required = false) Optional<Integer> page
    ) {
        List<TopCategory> topCategories = categoryService.getTopCategoryList();
        List<SubCategory> subCategories = categoryService.getSubCategoryList();
        List<TradeBoard> tradeBoards;
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() - 1 : 0, 10, Sort.by("createdDate").descending());
        Page<TradeBoard> sellBoardPage;

        if (!category.isPresent()) {
            sellBoardPage = tradeBoardService.getAllByBoardType(boardType, pageable);
        } else {
            sellBoardPage = tradeBoardService.getBoardsByBoardTypeAndSubCategory(boardType, category, pageable);
        }

        tradeBoards = sellBoardPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("sub", subCategories);
        response.put("top", topCategories);
        response.put("sellBoards", tradeBoards);
        response.put("totalPages", sellBoardPage.getTotalPages());
        response.put("page", sellBoardPage.getNumber());

        return response;
    }

    @GetMapping("/boardDetail/{boardId}")
    public String tradeBoardDetail() {
        return "../static/index";
    }

    @GetMapping("/api/boardDetail/{boardId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDetail(@PathVariable("boardId") Long boardId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            TradeBoard tradeBoard = tradeBoardService.findById(boardId);
            List<TradeBoardImg> imageList = tradeBoardService.getImgList(boardId);
            String nickName = tradeBoard.getUser().getNickName();
            HttpSession session = request.getSession();
            boolean isAuthor = tradeBoard.getUser().getEmail().equals(session.getAttribute("userEmail"));
            response.put("isAuthor", isAuthor);
            response.put("tradeBoard", tradeBoard);
            response.put("nickName", nickName);
            response.put("imageList", imageList);
            return ResponseEntity.ok(response); // 200 OK 응답 반환
        } catch (AccessDeniedException e) {
            response.put("error", "해당 게시글의 읽기 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 403 Forbidden 오류 반환
        } catch (EntityNotFoundException e) {
            response.put("error", "TradeBoard not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 Not Found 오류 반환
        }
    }



    @GetMapping("/writeForm/**")
    public String writeForm() {
        return "../static/index";
    }

    @GetMapping({"/api/writeForm"})
    @ResponseBody
    public Map<String, Object> getWriteForm(HttpServletRequest request, Optional<Long> boardId) {

        Map<String, Object> response = new HashMap<>();
        List<TopCategory> topCategories = categoryService.getTopCategoryList();
        List<SubCategory> subCategories = categoryService.getSubCategoryList();
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        response.put("csrfToken", csrfToken.getToken());
        response.put("sub", subCategories);
        response.put("top", topCategories);
        return response;
    }


    @PostMapping("/tradeWrite")
    @ResponseBody
    public ResponseEntity<?> tradeSave(@Valid TradeBoardDto tradeBoardDto,
                                       BindingResult bindingResult,
                                       @RequestPart("fileUpload") List<MultipartFile> mf,
                                       HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        if (bindingResult.hasErrors()) {
            errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        }
        if (tradeBoardDto.getSubCategory() == null) {
            errors.put("subCategory", "서브 카테고리를 선택해주세요.");
        }

        List<String> filenames = new ArrayList<>();
        HttpSession session = request.getSession();
        boolean isEmpty = true;
        if (mf.size() > 0) {
            for (MultipartFile file : mf) {
                if (!file.isEmpty() || file.getContentType().startsWith("image")) {
                    isEmpty = false;
                    break;
                }
            }
        }
        if (isEmpty) {
            errors.put("fileUpload", "이미지 파일은 필수입니다.");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        } else {
            Long boardId;
            Long userId = (Long) session.getAttribute("userId");
            try {
                boardId = tradeBoardService.saveBoard(userId, tradeBoardDto, mf);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            return ResponseEntity.ok(boardId);
        }
    }

    @PostMapping("/tradeUpdate/{boardId}")
    @ResponseBody
    public ResponseEntity<?> tradeUpdate(@PathVariable("boardId") Long boardId,
                                         @Valid TradeBoardDto tradeBoardDto, BindingResult bindingResult,
                                         @RequestPart(value = "fileUpload", required = false) List<MultipartFile> mf,
                                         @RequestParam("imageUrls") String imageUrlsJson,
                                         HttpSession session) throws JsonProcessingException {
        List<String> imageUrls = new ObjectMapper().readValue(imageUrlsJson, new TypeReference<>() {
        });
        Map<String, String> errors = new HashMap<>();
        if (bindingResult.hasErrors()) {
            errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        }
        if (tradeBoardDto.getSubCategory() == null) {
            errors.put("subCategory", "서브 카테고리를 선택해주세요.");
        }
        List<String> filenames = new ArrayList<>();
        boolean isImages = true;
        if (mf != null) {
            for (MultipartFile file : mf) {
                if (!file.getContentType().startsWith("image")) {
                    isImages = false;
                    break;
                }
            }
        }
        if (!isImages) {
            errors.put("fileUpload", "이미지 파일만 첨부 가능합니다.");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        } else {
            try {
                Long userId = (Long) session.getAttribute("userId");
                if (mf != null) {
                    tradeBoardService.updateBoard(userId, boardId, tradeBoardDto, mf, imageUrls);
                } else {
                    tradeBoardService.updateWithoutMultiFile(userId, boardId, tradeBoardDto, imageUrls);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            return ResponseEntity.ok(boardId);
        }
    }
}