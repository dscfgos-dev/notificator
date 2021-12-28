package com.uncledavecode.notificator.repository;

import com.uncledavecode.notificator.model.Profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
}
