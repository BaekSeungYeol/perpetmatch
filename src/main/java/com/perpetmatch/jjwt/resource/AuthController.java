package com.perpetmatch.jjwt.resource;

import com.perpetmatch.Domain.User;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.Role.RoleRepository;
import com.perpetmatch.infra.config.AppProperties;
import com.perpetmatch.jjwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    private final UserService userService;

    private final AppProperties appProperties;


    // 회원 임시 탈퇴
    @Transactional
    @DeleteMapping("/user/{usernameOrEmail}")
    public ResponseEntity dUser(@PathVariable String usernameOrEmail) {
        Optional<User> user = userRepository.findByNicknameOrEmail(usernameOrEmail,usernameOrEmail);
        user.ifPresent(u -> {
                    userRepository.delete(u);
                }
        );

        return ResponseEntity.ok().body(new ApiResponse(true, "회원 탈퇴 되었습니다."));

    }

    @PostMapping("/signin")
    @Transactional
    public ResponseEntity<?> authenticateMember(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        String usernameOrEmail = loginRequest.getUsernameOrEmail();
        Optional<User> user = userRepository.findByNicknameOrEmail(usernameOrEmail, usernameOrEmail);
        String nickname = user.get().getNickname();
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt,nickname));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerMember(@Valid @RequestBody SignUpRequest signUpRequest) {

        if(userRepository.existsByNickname(signUpRequest.getNickname())) {
            return new ResponseEntity<>(new ApiResponse(false, "해당 이름이 이미 존재합니다."),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "이메일이 이미 존재합니다."),
                    HttpStatus.BAD_REQUEST);
        }

        User result = userService.join(signUpRequest);

        return ResponseEntity.ok().body(new ApiResponse(true, "회원가입이 성공적으로 완료되었습니다."));
    }

}
