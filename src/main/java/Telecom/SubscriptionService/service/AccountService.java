package Telecom.SubscriptionService.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Telecom.SubscriptionService.dto.AccountDto;
import Telecom.SubscriptionService.model.Account;
import Telecom.SubscriptionService.model.User;
import Telecom.SubscriptionService.repository.AccountRepository;
import Telecom.SubscriptionService.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Account getAccountByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getAccount() : null;
    }

    public Account updateAccount(Long id, AccountDto accountDto) {
        return accountRepository.findById(id).map(account -> {
            account.setBalance(accountDto.getBalance());
            account.setDetails(accountDto.getDetails());
            accountRepository.save(account);
            return account;
        }).orElse(null);
    }

    public void createAccount(AccountDto accountDto) {
        User user = accountDto.getUser();
        if (user != null) {
            Account account = new Account();
            account.setBalance(accountDto.getBalance());
            account.setDetails(accountDto.getDetails());
            account.setUser(user);
            accountRepository.save(account);
        }
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
