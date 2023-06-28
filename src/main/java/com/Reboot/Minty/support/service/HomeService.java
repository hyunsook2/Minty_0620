package com.Reboot.Minty.support.service;

import com.Reboot.Minty.support.dto.HomeDto;
import com.Reboot.Minty.support.entity.Home;
import com.Reboot.Minty.support.repository.HomeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HomeService {

    @Autowired
    HomeRepository homeRepository;

    public void homeSave(Home home){
        homeRepository.save(home);
    }

    public List<HomeDto> getHome() {
        List<Home> homes = homeRepository.findAll();
        List<HomeDto> homeDtos = new ArrayList<>();

        for (Home home : homes) {
            HomeDto homeDto = HomeDto.builder()
                    .id(home.getId())
                    .title(home.getTitle())
                    .content(home.getContent())
                    .build();
            homeDtos.add(homeDto);
        }

        return homeDtos;
    }

    @Transactional
    public void deleteHome(Long id){
        Home home = homeRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException());

        homeRepository.deleteById(home.getId());
    }
}
