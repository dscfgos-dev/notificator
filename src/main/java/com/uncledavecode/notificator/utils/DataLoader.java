package com.uncledavecode.notificator.utils;

import java.util.List;

import com.uncledavecode.notificator.model.Profile;
import com.uncledavecode.notificator.repository.ProfileRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner{

    private final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private ProfileRepository profileRepository;
    
    public DataLoader(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void run(String... args) throws Exception {
       Profile pro1 = new Profile("L1", "Level 1");
       Profile pro2 = new Profile("L3", "Level 2");
       Profile pro3 = new Profile("L4", "Level 3");

       this.profileRepository.saveAll(List.of(pro1,pro2,pro3));

       logger.info(this.profileRepository.findAll().size() +" Profiles Loaded!");
    }
}
