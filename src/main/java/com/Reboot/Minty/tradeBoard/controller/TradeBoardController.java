package com.Reboot.Minty.tradeBoard.controller;

import com.Reboot.Minty.addressCode.dto.AddressCodeDto;
import com.Reboot.Minty.addressCode.repository.AddressCodeRepository;
import com.Reboot.Minty.categories.CategoryService;
import com.Reboot.Minty.categories.dto.SubCategoryDto;
import com.Reboot.Minty.categories.dto.TopCategoryDto;
import com.Reboot.Minty.member.dto.UserLocationResponseDto;
import com.Reboot.Minty.member.repository.UserLocationRepository;
import com.Reboot.Minty.tradeBoard.dto.*;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardRepository;
import com.Reboot.Minty.tradeBoard.service.TradeBoardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TradeBoardController {
    private final CategoryService categoryService;
    private final TradeBoardService tradeBoardService;
    private final TradeBoardRepository tradeBoardRepository;
    private final AddressCodeRepository addressCodeRepository;


    @Autowired
    public TradeBoardController(CategoryService categoryService, TradeBoardService tradeBoardService, TradeBoardRepository tradeBoardRepository, AddressCodeRepository addressCodeRepository, UserLocationRepository userLocationRepository) {
        this.categoryService = categoryService;
        this.tradeBoardService = tradeBoardService;
        this.tradeBoardRepository = tradeBoardRepository;
        this.addressCodeRepository = addressCodeRepository;
    }


    @GetMapping("/boardList/**")
    public String boardList(Model model) {
        return "../static/index";
    }


    @GetMapping(value = {
            "/api/boardList/",
            "/api/boardList/page/{page}",
            "/api/boardList/searchArea/{searchArea}/page/{page}",
            // 1 필터
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/sortBy/{sortBy}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/minPrice/{minPrice}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/maxPrice/{maxPrice}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/searchQuery/{searchQuery}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/page/{page}", // 추가

            // 2 필터
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/sortBy/{sortBy}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/minPrice/{minPrice}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/maxPrice/{maxPrice}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/searchQuery/{searchQuery}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/minPrice/{minPrice}/sortBy/{sortBy}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/minPrice/{minPrice}/searchQuery/{searchQuery}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/minPrice/{minPrice}/maxPrice/{maxPrice}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/maxPrice/{maxPrice}/sortBy/{sortBy}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/maxPrice/{maxPrice}/searchQuery/{searchQuery}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/searchQuery/{searchQuery}/sortBy/{sortBy}/page/{page}",

            // 3 필터
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/searchQuery/{searchQuery}/sortBy/{sortBy}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/searchQuery/{searchQuery}/minPrice/{minPrice}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/searchQuery/{searchQuery}/maxPrice/{maxPrice}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/minPrice/{minPrice}/maxPrice/{maxPrice}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/minPrice/{minPrice}/sortBy/{sortBy}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/minPrice/{minPrice}/searchQuery/{searchQuery}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/maxPrice/{maxPrice}/searchQuery/{searchQuery}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/maxPrice/{maxPrice}/sortBy/{sortBy}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/searchQuery/{searchQuery}/minPrice/{minPrice}/maxPrice/{maxPrice}/page/{page}",
            // 4 필터
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/searchQuery/{searchQuery}/minPrice/{minPrice}/maxPrice/{maxPrice}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/searchQuery/{searchQuery}/minPrice/{minPrice}/sortBy/{sortBy}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/searchQuery/{searchQuery}/maxPrice/{maxPrice}/sortBy/{sortBy}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/minPrice/{minPrice}/maxPrice/{maxPrice}/sortBy/{sortBy}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/minPrice/{minPrice}/maxPrice/{maxPrice}/sortBy/{sortBy}/page/{page}",
            "/api/boardList/searchArea/{searchArea}/searchQuery/{searchQuery}/minPrice/{minPrice}/maxPrice/{maxPrice}/sortBy/{sortBy}/page/{page}",
            // 모든 필터
            "/api/boardList/searchArea/{searchArea}/category/{subCategoryId}/searchQuery/{searchQuery}/minPrice/{minPrice}/maxPrice/{maxPrice}/sortBy/{sortBy}/page/{page}"
    })
    @ResponseBody
    public Map<String, Object> getBoardList(
            HttpServletRequest request,
            TradeBoardSearchDto tradeBoardSearchDto,
            @PathVariable(value = "page", required = false) Optional<Integer> page,
            @RequestParam(value = "subCategoryId", required = false) Optional<Long> subCategoryId,
            @RequestParam(value = "searchQuery", required = false) Optional<String> searchQuery,
            @RequestParam(value = "minPrice", required = false) Optional<Integer> minPrice,
            @RequestParam(value = "maxPrice", required = false) Optional<Integer> maxPrice,
            @RequestParam(value = "sortBy", required = false) Optional<String> sortBy,
            @PathVariable(value = "searchArea", required = false) Optional<String> searchArea
    ) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");
        List<UserLocationResponseDto> userLocationList = tradeBoardService.getLogginedLocationList(userId);
        if (subCategoryId.isPresent()) {
            tradeBoardSearchDto.setSubCategoryId(subCategoryId.get());
        }
        if (searchQuery.isPresent()) {
            tradeBoardSearchDto.setSearchQuery(searchQuery.get());
        }
        if (minPrice.isPresent()) {
            tradeBoardSearchDto.setMinPrice(minPrice.get());
        }
        if (maxPrice.isPresent()) {
            tradeBoardSearchDto.setMaxPrice(maxPrice.get());
        }
        if (sortBy.isPresent()) {
            tradeBoardSearchDto.setSortBy(sortBy.get());
        }
        if (searchArea.isPresent()) {
            System.out.println("1" + searchArea.get());
            tradeBoardSearchDto.setSearchArea(searchArea.get());
        } else if (!searchArea.isPresent()) {
            System.out.println("2" + tradeBoardSearchDto.getSearchArea());
            tradeBoardSearchDto.setSearchArea(userLocationList.get(0).getAddress());
        }

        List<TopCategoryDto> topCategories = categoryService.getTopCategoryList();
        List<SubCategoryDto> subCategories = categoryService.getSubCategoryList();

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 20);
        Slice<TradeBoardDto> tradeBoards = tradeBoardService.getTradeBoard(tradeBoardSearchDto, pageable);
        System.out.println("isEmpty?" + tradeBoards.isEmpty());
        System.out.println("hasNext?" + tradeBoards.hasNext());
        System.out.println(tradeBoards.getNumber());
        Map<String, Object> response = new HashMap<>();
        response.put("userLocationList", userLocationList);
        response.put("sub", subCategories);
        response.put("top", topCategories);
        response.put("tradeBoards", tradeBoards.getContent());
        response.put("hasNext", tradeBoards.hasNext());
        response.put("page", tradeBoards.getNumber());

        return response;
    }


    @GetMapping("/boardDetail/{boardId}")
    public String tradeBoardDetail() {
        return "../static/index";
    }

    @GetMapping("/api/boardDetail/{boardId}")
    @ResponseBody
    public ResponseEntity<?> getDetail(@PathVariable("boardId") Long boardId, HttpServletRequest request) {

        try {
            TradeBoardDetailDto tradeBoard = tradeBoardService.findById(boardId);
            List<TradeBoardImgDto> imageList = tradeBoardService.getImgList(boardId);

            String nickName = tradeBoard.getUser().getNickName();
            HttpSession session = request.getSession();
            boolean isAuthor = tradeBoard.getUser().getEmail().equals(session.getAttribute("userEmail"));
            System.out.println("isAuthor>>" + isAuthor);
            System.out.println(nickName);
            TradeBoardDetailResponseDto response = new TradeBoardDetailResponseDto();
            response.setAuthor(isAuthor);
            response.setTradeBoard(tradeBoard);
            response.setNickName(nickName);
            response.setImageList(imageList);
            return ResponseEntity.ok().body(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/writeForm/**")
    public String writeForm() {
        return "../static/index";
    }

    @GetMapping({"/api/writeForm"})
    @ResponseBody
    public Map<String, Object> getWriteForm(HttpServletRequest request, Optional<Long> boardId) {
        HttpSession session = request.getSession();
        Long userId= (Long) session.getAttribute("userId");
        Map<String, Object> response = new HashMap<>();
        List<AddressCodeDto> addressCode = addressCodeRepository.findAll().stream().map(AddressCodeDto::of).collect(Collectors.toList());
        List<TopCategoryDto> topCategories = categoryService.getTopCategoryList();
        List<SubCategoryDto> subCategories = categoryService.getSubCategoryList();
        List<UserLocationResponseDto> userLocationList = tradeBoardService.getLogginedLocationList(userId);
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        response.put("userLocationList",userLocationList);
        response.put("addressCode", addressCode);
        response.put("csrfToken", csrfToken.getToken());
        response.put("sub", subCategories);
        response.put("top", topCategories);
        return response;
    }


    @PostMapping("/tradeWrite")
    @ResponseBody
    public ResponseEntity<?> tradeSave(@Valid TradeBoardFormDto tradeBoardFormDto,
                                       BindingResult bindingResult,
                                       @RequestPart("fileUpload") List<MultipartFile> mf,
                                       HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        if (bindingResult.hasErrors()) {
            errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        }
        if (tradeBoardFormDto.getSubCategory() == null) {
            errors.put("subCategory", "서브 카테고리를 선택해주세요.");
        }

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
                boardId = tradeBoardService.saveBoard(userId, tradeBoardFormDto, mf);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            return ResponseEntity.ok(boardId);
        }
    }

    @PostMapping("/tradeUpdate/{boardId}")
    @ResponseBody
    public ResponseEntity<?> tradeUpdate(@PathVariable("boardId") Long boardId,
                                         @Valid TradeBoardFormDto tradeBoardFormDto, BindingResult bindingResult,
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
        if (tradeBoardFormDto.getSubCategory() == null) {
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
                    tradeBoardService.updateBoard(userId, boardId, tradeBoardFormDto, mf, imageUrls);
                } else {
                    tradeBoardService.updateWithoutMultiFile(userId, boardId, tradeBoardFormDto, imageUrls);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            return ResponseEntity.ok(boardId);
        }
    }

    @PostMapping("/api/tradeBoard/deleteRequest")
    @ResponseBody
    public ResponseEntity<?> deleteRequest(@RequestBody Long tradeBoardId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            tradeBoardService.deleteBoardRequest(tradeBoardId, userId);
            return ResponseEntity.ok().body("해당 물품 삭제를 완료 하였습니다.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Value("${kaKao-service.key}")
    private String kaKaoKey;

    @PostMapping("/api/kakao/location")
    @ResponseBody
    public Mono<ResponseEntity<?>> getLocationFromKakaoApi(@RequestBody LocationRequest request) {
        String apiUrl = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x="
                + request.getLongitude() + "&y=" + request.getLatitude();

        WebClient client = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + kaKaoKey)
                .build();

        return client.get()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.ok().body(body));
                    } else {
                        return Mono.just(ResponseEntity.status(response.statusCode()).build());
                    }
                });
    }
}