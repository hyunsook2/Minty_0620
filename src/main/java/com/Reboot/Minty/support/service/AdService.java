package com.Reboot.Minty.support.service;

import com.Reboot.Minty.support.entity.Ad;
import com.Reboot.Minty.support.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AdService {
    private final AdRepository adRepository;

    @Autowired
    public AdService(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    public void saveAd(Ad ad) {
        adRepository.save(ad);
    }

    public List<Ad> getAllAds() {
        List<Ad> ads = adRepository.findAll();
        for (Ad ad : ads) {
            if (ad.getRequestDate() == null) {
                ad.setRequestDate(LocalDate.now());
            }
        }
        return ads;
    }

    public Optional<Ad> getAdById(Long id) {
        return adRepository.findById(id);
    }

    public void updateAdStatus(Long id, String status) {
        Optional<Ad> adOptional = adRepository.findById(id);
        if (adOptional.isPresent()) {
            Ad ad = adOptional.get();
            ad.setStatus(status);
            adRepository.save(ad);
        } else {
            throw new RuntimeException("Ad not found with id: " + id);
        }
    }


}
