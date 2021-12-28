package com.uncledavecode.notificator.services;

import java.util.List;

import com.uncledavecode.notificator.model.Profile;
import com.uncledavecode.notificator.repository.ProfileRepository;

import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public List<Profile> getAllProfiles() {
        return this.profileRepository.findAll();
    }

}
