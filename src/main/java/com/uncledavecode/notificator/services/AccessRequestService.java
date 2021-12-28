package com.uncledavecode.notificator.services;

import java.util.List;

import com.uncledavecode.notificator.model.AccessRequest;
import com.uncledavecode.notificator.repository.AccessRequestRepository;

import org.springframework.stereotype.Service;

@Service
public class AccessRequestService {

    private final AccessRequestRepository accessRequestRepository;

    public AccessRequestService(AccessRequestRepository accessRequestRepository) {
        this.accessRequestRepository = accessRequestRepository;
    }

    public AccessRequest getByChatId(Long chatId) {
        if (chatId != null) {
            return this.accessRequestRepository.findByChatId(chatId);
        } else {
            throw new IllegalArgumentException("chatId cannot be null");
        }
    }

    public AccessRequest updateAccessRequest(AccessRequest accessRequest) {
        if (accessRequest != null) {
            return this.accessRequestRepository.save(accessRequest);
        } else {
            throw new IllegalArgumentException("accessRequest cannot be null");
        }
    }

    public List<AccessRequest> getAllAccessRequests(){
        return this.accessRequestRepository.findAll();
    }
}
