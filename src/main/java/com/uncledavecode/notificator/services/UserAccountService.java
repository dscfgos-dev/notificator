package com.uncledavecode.notificator.services;

import java.util.List;

import com.uncledavecode.notificator.model.UserAccount;
import com.uncledavecode.notificator.repository.UserAccountRepository;

import org.springframework.stereotype.Service;

@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;

    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount getByChatId(Long chatId) {
        if (chatId != null) {
            return this.userAccountRepository.findByChatId(chatId);
        } else {
            throw new IllegalArgumentException("chatId cannot be null");
        }
    }

    public UserAccount updateUserAccount(UserAccount userAccount) {
        if (userAccount != null) {
            return this.userAccountRepository.save(userAccount);
        } else {
            throw new IllegalArgumentException("userAccount cannot be null");
        }
    }

    public List<UserAccount> getAllUserAccounts() {
        return this.userAccountRepository.findAll();
    }
}
