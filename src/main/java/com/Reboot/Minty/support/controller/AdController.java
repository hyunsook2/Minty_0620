package com.Reboot.Minty.support.controller;

import com.Reboot.Minty.manager.dto.AdDto;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.support.entity.Ad;
import com.Reboot.Minty.support.repository.AdRepository;
import com.Reboot.Minty.support.service.AdService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdController {
    private final AdService adService;
    private final UserService userService;
    public AdController(AdService adService, UserService userService){
        this.adService = adService;
        this.userService = userService;
    }

    @Autowired
    private AdRepository adRepository;


    @GetMapping("/marketingBoard")
    @ResponseBody
    public ResponseEntity<?> showAdBoard(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Ad> adPage = adRepository.findAll(pageable);
        List<Ad> ads = adPage.getContent();

        List<AdDto> adDTOs = new ArrayList<>();
        for (Ad ad : ads) {
            AdDto adDto = new AdDto();
            adDto.convertToDTO(ad);
            adDto.setTotalPages(adPage.getTotalPages());
            adDTOs.add(adDto);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("adList", adDTOs);
        response.put("currentPage", page);
        response.put("pageSize", pageSize);
        response.put("totalPages", adPage.getTotalPages());

        return ResponseEntity.ok(response);
    }



    @GetMapping("/adWrite")
    public String AdWriteForm(Model model){
        model.addAttribute("ad", new Ad());
        return "ad/adWrite";
    }

    @PostMapping("/adWrite")
    public String submitAdForm(@ModelAttribute Ad ad, @RequestParam("imageFile") MultipartFile imageFile, HttpSession session) {
        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                String filePath = "src/main/resources/static/adimage/"; // Replace with the actual file path where you want to save the images
                String absolutePath = new File("").getAbsolutePath();
                String savedFileName = absolutePath + File.separator + filePath + fileName;
                //String savedFileName = filePath + fileName;
                imageFile.transferTo(new File(savedFileName));

                ad.setImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LocalDate currentDate = LocalDate.now();
        ad.setRequestDate(currentDate);
        ad.setEndDate(ad.getStartDate().plusDays(ad.getDuration()));

        if (ad.getDuration() == 7) {
            ad.setAmount(500000);
        } else if (ad.getDuration() == 30) {
            ad.setAmount(2000000);
        } else if (ad.getDuration() == 90) {
            ad.setAmount(5000000);
        } else if (ad.getDuration() == 180) {
            ad.setAmount(8000000);
        } else if (ad.getDuration() == 365) {
            ad.setAmount(15000000);
        }

        adService.saveAd(ad);

        return "redirect:/";
    }

}
