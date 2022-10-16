package com.imjustdoom.justdoomapi.service;

import com.imjustdoom.justdoomapi.dto.in.LoginDto;
import com.imjustdoom.justdoomapi.dto.in.RegisterDto;
import com.imjustdoom.justdoomapi.model.Account;
import com.imjustdoom.justdoomapi.model.Token;
import com.imjustdoom.justdoomapi.repository.AccountRepository;
import com.imjustdoom.justdoomapi.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return accountRepository.findByUsernameEqualsIgnoreCase(s).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public ResponseEntity<?> login(LoginDto loginDto, String token) {
        if (token != null && !token.equals("") && !token.equals("undefined")) {
            return ResponseEntity.ok().body("{\"error\": \"You are already logged in, please log out\"}");
        }

        if (loginDto.getName() == null) return ResponseEntity.notFound().build();

        Optional<Account> account = accountRepository.findByUsernameEqualsIgnoreCase(loginDto.getName());

        if (account.isEmpty()) {
            return ResponseEntity.ok().body("{\"error\": \"Account not found\"}");
        }

        if (!BCrypt.checkpw(loginDto.getPassword(), account.get().getPassword())) {
            return ResponseEntity.ok().body("{\"error\": \"Incorrect password\"}");
        }

        Token cookieToken = new Token("0.0.0.0", account.get());
        tokenRepository.save(cookieToken);

        // Try fix httpOnly true maybe
        ResponseCookie cookie = ResponseCookie.from("token", cookieToken.getToken()).path("/").httpOnly(false).maxAge(604800).sameSite("None").secure(false).build();

        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).build();
    }

    public ResponseEntity<?> register(RegisterDto registerDto, String token) {
        if (token != null && !token.equals("")) {
            return ResponseEntity.ok().body("{\"error\": \"You are already logged in, please log out\"}");
        }

        if (registerDto.getName() == null) return ResponseEntity.notFound().build();

        // check if already exists

        Optional<Account> checkAccount = accountRepository.findByUsernameEqualsIgnoreCase(registerDto.getName());

        if (checkAccount.isPresent()) {
            return ResponseEntity.ok().body("{\"error\": \"Account already exists\"}");
        }

        checkAccount = accountRepository.findByEmailEqualsIgnoreCase(registerDto.getEmail());

        if (checkAccount.isPresent()) {
            return ResponseEntity.ok().body("{\"error\": \"Email already exists\"}");
        }

        Account account = new Account(registerDto.getName(), registerDto.getEmail(), this.passwordEncoder.encode(registerDto.getPassword()));
        accountRepository.save(account);

        Token cookieToken = new Token("0.0.0.0", account);
        tokenRepository.save(cookieToken);

        // Try fix httpOnly true maybe
        ResponseCookie cookie = ResponseCookie.from("token", cookieToken.getToken()).path("/").httpOnly(false).maxAge(604800).sameSite("None").secure(false).build();

        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).build();
    }

    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().header("Set-Cookie", ResponseCookie.from("token", "").path("/").httpOnly(false).maxAge(0).sameSite("None").secure(false).build().toString()).build();
    }

    public boolean doesAccountHaveAdminPermission(Account account) {
        return account.getRole().equals("ADMIN");
    }
}