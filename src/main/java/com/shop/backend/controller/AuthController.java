package com.shop.backend.controller;


import com.shop.backend.dto.AddressDTO;
import com.shop.backend.dto.UserUpdateDTO;
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
import com.shop.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
            if(roleStr.equals("admin")) {
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




        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getAddresses(),
                user.getPhoneNumber(),
                user.getPoints(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.isTwoFactorEnabled(),
                roles,
                user.getName(),
                user.getCreatedDate()

        );

        return ResponseEntity.ok().body(response);
    }


    //인증된 유저 네임
    @GetMapping("/username")
    @PreAuthorize("isAuthenticated()")
    public String getUsername(Principal principal) {
        return principal.getName() != null ? principal.getName() : "";
    }


    @PutMapping("/user")
    public ResponseEntity<?> updateUserDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressDTO updateRequest) {

        // 현재 로그인한 유저 정보 가져오기
        User user = userService.findByUsername(userDetails.getUsername());
        user.setName(updateRequest.getName());
        user.setPhoneNumber(updateRequest.getPhoneNumber());


        // 기존 주소 업데이트 (첫 번째 주소만 수정 가능)
        if (user.getAddresses().size() > 0) {
            Address address = user.getAddresses().get(0);
            address.setPostcode(updateRequest.getPostcode());
            address.setAddress(updateRequest.getAddress());
            address.setDetailAddress(updateRequest.getDetailAddress());
            address.setExtraAddress(updateRequest.getExtraAddress());
        }

        // 저장 후 응답 반환
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("정보가 성공적으로 수정되었습니다.!"));
    }

    //이메일로 보내기
    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try{
            userService.generatePasswordResetToken(email);
            return ResponseEntity.ok(new MessageResponse("Password reset email sent!"));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error sending password reset email"));
        }
    }

    //리셋 토큰확인/ 패스워드 리셋
    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                           @RequestParam String newPassword) {
        try{
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new MessageResponse("패스워드 초기화 완료"));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    //이메일 검증
    @GetMapping("/public/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email,
                                         @RequestParam String code) {
        boolean isValid = userService.verifyEmailCode(email, code);
        return isValid ? ResponseEntity.ok("이메일 인증 성공")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 실패");
    }




    @PostMapping("/public/send-email")
    public ResponseEntity<?> successEmail(@RequestParam String email) {
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 이메일입니다.");
        }
        userService.generateEmailResetToken(email);
        return ResponseEntity.ok("이메일 인증코드가 전송되었습니다.");

    }


    @PostMapping("/public/reset-email")
    public ResponseEntity<?> resetEmail(@RequestParam String token,
                                        @RequestParam String newEmail) {
        try{
            userService.resetPassword(token, newEmail);
            return ResponseEntity.ok(new MessageResponse("Email reset successful"));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }


}




