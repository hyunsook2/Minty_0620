package com.Reboot.Minty.support.controller;

import com.Reboot.Minty.support.dto.HomeDto;
import com.Reboot.Minty.support.entity.Home;
import com.Reboot.Minty.support.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    HomeService homeService;

    @GetMapping("/supportHome")
    public String home(Model model){
        List<HomeDto> homeDtos = homeService.getHome();
        model.addAttribute("homeDtos", homeDtos);
        return "support/supportHome";
    }

    @GetMapping(value = "/home/new")
    public String homeForm(Model model) {
        model.addAttribute("homeDto", new HomeDto());
        return "/support/homeSave";
    }

    @PostMapping(value = "/home/new")
    public String homeSaveForm(HomeDto homeDto){
        Home home = homeDto.toEntity();
        homeService.homeSave(home);
        return "redirect:/supportHome";
    }

    @GetMapping("/home/delete/{id}")
    public String deleteHome(@PathVariable("id") Long id) {
        homeService.deleteHome(id);
        return "redirect:/supportHome";
    }

}
