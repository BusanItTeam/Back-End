package com.shop.backend.controller;


import com.shop.backend.models.AppRole;
import com.shop.backend.models.Role;
import com.shop.backend.models.User;
import com.shop.backend.repository.RoleRepository;
import com.shop.backend.repository.UserRepository;
import com.shop.backend.security.jwt.JwtUtils;
import com.shop.backend.security.request.LoginRequest;
import com.shop.backend.security.request.SignupRequest;
import com.shop.backend.security.response.LoginResponse;
import com.shop.backend.security.response.MessageResponse;
import com.shop.backend.security.response.UserInfoResponse;
import com.shop.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auths")
public class AuthController {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager; //인증매니저

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserService userService;

    //로그인 아 진짜 짜
    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try{
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        }catch (AuthenticationException exception){
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }


            // 시큐리티 인증됨
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 인증된 유저디테일 가져옴
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 인증된 유저에 jwt 토큰 생성하기
            String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

            // 유저의 권한 리스트 가져오기
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            //유저이름 유저권한 jwt 토큰으로 새 객체를 만듬
            LoginResponse response = new LoginResponse(userDetails.getUsername(),
                    roles, jwtToken);

            // response body 로 JWT 토큰을 포함한 response 객체로 리턴
            return ResponseEntity.ok(response);

    }
    //회원가입
    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        //유저네임 중복방지
        if (userRepository.existsByUserName(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username already exists"));
        }
        //이메일 중복방지
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email already exists"));
        }

        //가입하기(일단 .. 유저네임 이메일, 패스워드만 머리아픔..)
        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));
        
        //권한 리스트(시큐리티 유저 저장시 권한 리스트 필요)
        Set<String> strRoles = signupRequest.getRole();
        Role role;
        
        //클라이언트에서 문자열 "admin" 일 경우 관리가 권한 나머지는 유저권한

        if (strRoles == null || strRoles.isEmpty()) {
            role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        }else{
            String roleStr = strRoles.iterator().next();
            if(roleStr.equals("admin")){
                role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found"));
            }else{
                role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found"));
            }

            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            user.setEnabled(true);
            user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
            user.setAccountExpiryDate(LocalDate.now().plusYears(1));
            user.setTwoFactorEnabled(false);
            user.setSignUpMethod("email"); //이메일 가입방법
        }
        user.setRole(role);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    //인증된 유저 정보 가져오기
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.isTwoFactorEnabled(),
                roles
        );

        return ResponseEntity.ok().body(response);
    }

    //인증된 유저 네임
    @GetMapping("/username")
    public String getUsername(Principal principal) {
        return principal.getName() != null ? principal.getName() : "";
    }
}
