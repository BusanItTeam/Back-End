package com.shop.backend.controller;


import com.shop.backend.models.Address;
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
import com.shop.backend.security.services.UserDetailsImpl;
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
import java.util.*;
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
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
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
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        //유저네임 중복방지
        if (userRepository.existsByUserName(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username already exists"));
        }
        //이메일 중복방지
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email already exists"));
        }

        // 유저 객체 생성 (일단 유저네임, 이메일, 패스워드만)
        User user = new User(signupRequest.getUsername(),signupRequest.getName(), signupRequest.getEmail(),
                signupRequest.getPhoneNumber(),
                encoder.encode(signupRequest.getPassword()));

        // 권한 설정
        Set<String> strRoles = signupRequest.getRole();
        Role role;

        if (strRoles == null || strRoles.isEmpty()) {
            role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            String roleStr = strRoles.iterator().next();
            if (roleStr.equals("admin")) {
                role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found"));
            } else {
                role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found"));
            }
        }

        user.setRole(role);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        user.setAccountExpiryDate(LocalDate.now().plusYears(1));
        user.setTwoFactorEnabled(false);
        user.setSignUpMethod("email"); // 이메일 가입

        // 1. 먼저 User를 저장
        User savedUser = userRepository.save(user);

        // 2. Address를 User와 연결해서 저장
        Address address = new Address(signupRequest.getPostcode(), signupRequest.getAddress(),
                signupRequest.getDetailAddress(), signupRequest.getExtraAddress(), savedUser);

        savedUser.addAddress(address); // 양방향 관계 설정

        // 3. User를 다시 저장 (주소 포함)
        userRepository.save(savedUser);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


    @GetMapping("/user")
    //인증된 유저 정보 가져오기
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        // 주소 정보 출력
        for (Address address : user.getAddresses()) {  // List<Address> 타입으로 처리
            System.out.println("어드레스: " + address.getId() + ", "
                    + address.getPostcode() + ", "
                    + address.getDetailAddress() + " ("
                    + address.getExtraAddress() + ")");
        }

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.isTwoFactorEnabled(),
                roles,
                user.getName()
        );

        return ResponseEntity.ok().body(response);
    }


    //인증된 유저 네임
    @GetMapping("/username")
    public String getUsername(Principal principal) {
        return principal.getName() != null ? principal.getName() : "";
    }



}


