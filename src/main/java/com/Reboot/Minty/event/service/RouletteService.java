package com.Reboot.Minty.event.service;

import com.Reboot.Minty.event.entity.Roulette;
import com.Reboot.Minty.event.repository.RouletteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RouletteService {
    private final RouletteRepository rouletteRepository;

    @Autowired
    public RouletteService(RouletteRepository rouletteRepository) {
        this.rouletteRepository = rouletteRepository;
    }

    @Transactional
    public Roulette saveRoulette(Roulette roulette) {
        try {
            System.out.println("Saving roulette: " + roulette);
            return rouletteRepository.save(roulette);
        } catch (Exception e) {
            System.out.println("Error saving roulette: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
