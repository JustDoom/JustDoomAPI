package com.imjustdoom.justdoomapi.service;

import com.imjustdoom.justdoomapi.dto.in.LoginDto;
import com.imjustdoom.justdoomapi.dto.in.RegisterDto;
import com.imjustdoom.justdoomapi.dto.out.UserDto;
import com.imjustdoom.justdoomapi.model.Account;
import com.imjustdoom.justdoomapi.model.Token;
import com.imjustdoom.justdoomapi.repository.AccountRepository;
import com.imjustdoom.justdoomapi.repository.TokenRepository;
import com.imjustdoom.justdoomapi.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
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
        ResponseCookie cookie = ResponseCookie.from("token", cookieToken.getToken()).path("/").httpOnly(false).maxAge(604800).sameSite("None").secure(true).domain("").build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    public boolean isUsernameValid(String username) {
        return Pattern.matches("[^a-zA-Z0-9_]", username);
    }

    public ResponseEntity<?> register(RegisterDto registerDto, String token) {
        if (token != null && !token.equals("")) {
            return ResponseEntity.ok().body("{\"error\": \"You are already logged in, please log out\"}");
        }

        if (registerDto.getName() == null) return ResponseEntity.notFound().build();

        if (!ValidationUtil.isUsernameValid(registerDto.getName())) {
            return ResponseEntity.ok().body("{\"error\": \"Username is invalid\"}");
        }

        if (!ValidationUtil.isEmailValid(registerDto.getEmail())) {
            return ResponseEntity.ok().body("{\"error\": \"Email is invalid\"}");
        }

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

        ResponseCookie cookie = ResponseCookie.from("token", cookieToken.getToken()).path("/").httpOnly(false).maxAge(604800).sameSite("None").secure(true).domain("").build();

        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).build();
    }

    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().header("Set-Cookie", ResponseCookie.from("token", "").path("/").httpOnly(false).maxAge(0).sameSite("None").secure(true).domain("").build().toString()).build();
    }

    public ResponseEntity<?> getUserByToken(String token) {
        if (token == null || token.equals("")) {
            return ResponseEntity.ok().body("{\"error\": \"Token not specified\"}");
        }

        Optional<Token> cookieToken = tokenRepository.findByToken(token);

        if (cookieToken.isEmpty()) {
            return ResponseEntity.ok().body("{\"error\": \"No such account by that token\"}");
        }

        UserDto userDto = UserDto.create(cookieToken.get().getAccount().getUsername(), cookieToken.get().getAccount().getRole(), cookieToken.get().getAccount().getId());

        return ResponseEntity.ok().body(userDto);
    }

    public boolean doesAccountHaveAdminPermission(Account account) {
        return account.getRole().equals("ADMIN");
    }
}