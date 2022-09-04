package com.karrier.mentoring.controller;

import com.karrier.mentoring.dto.MemberFormDto;
import com.karrier.mentoring.dto.MemberManagePasswordDto;
import com.karrier.mentoring.dto.ParticipationStudentFormDto;
import com.karrier.mentoring.entity.*;
<<<<<<< Updated upstream
=======
import com.karrier.mentoring.http.BasicResponse;
import com.karrier.mentoring.http.SuccessDataResponse;
import com.karrier.mentoring.http.SuccessResponse;
import com.karrier.mentoring.http.error.ErrorCode;
import com.karrier.mentoring.http.error.exception.*;
>>>>>>> Stashed changes
import com.karrier.mentoring.repository.*;
import com.karrier.mentoring.service.FollowService;
import com.karrier.mentoring.service.MemberService;
import com.karrier.mentoring.service.ParticipationStudentService;
import com.karrier.mentoring.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@CrossOrigin("http://localhost:3000")
@RequestMapping("/members")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final S3Uploader s3Uploader;

    public static final String profileImageBaseUrl = "https://karrier.s3.ap-northeast-2.amazonaws.com/profile-image/";
    //회원가입 요청시
    @PostMapping(value = "/new")
    public ResponseEntity<Object> memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult) {

        //빈칸 있을 경우
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("blank error");
        }

        //비밀번호와 비밀번호 확인이 일치하지 않을 경우
        if (!memberFormDto.getPassword().equals(memberFormDto.getPasswordCheck())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("password check error");
        }

        //중복된 닉네임일 경우
        if (memberService.checkDuplicateNickName(memberFormDto.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate nickname");
        }

        try { // member 형태로 변환 후 데이터베이스에 member 정보 저장
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            Member newMember = memberService.saveMember(member);

<<<<<<< Updated upstream
            return ResponseEntity.status(HttpStatus.CREATED).body(newMember);
=======
            return ResponseEntity.status(HttpStatus.OK).body(new SuccessDataResponse<Member>(newMember));
>>>>>>> Stashed changes

        } catch (IllegalStateException e) { //이미 가입된 이메일일 경우

            return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate email");
        }
    }


    //로그인시 아이디 패스워드가 틀릴경우
    @GetMapping(value = "/login/error") 

    public ResponseEntity<String> loginError() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("wrong id or password");
    }

    //유저가 로그인시 로그인 시간 저장
    @GetMapping(value = "/update-login/{email}")
    public ResponseEntity<Member> updateLoginTime(@PathVariable("email") String email) {

        //로그인 한 회원 정보 찾기
        Member member = memberRepository.findByEmail(email);
        
        //로그인 날짜 업데이트 후 저장
        Member updatedMember = Member.updateRecentlyLoginDate(member);
        Member savedMember = memberService.modifyMember(updatedMember);

<<<<<<< Updated upstream
        return ResponseEntity.status(HttpStatus.OK).body(savedMember);
=======
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessDataResponse<Member>(savedMember));
>>>>>>> Stashed changes
    }

    //비밀번호 변경 요청시
    @PostMapping(value = "/manage/password")
    public ResponseEntity<Object> mentorManagePassword(@Valid MemberManagePasswordDto memberManagePasswordDto, BindingResult bindingResult) {

        //빈칸있을 경우
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("blank error");
        }
        
        //사용자 email 얻기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        //DB에 저장된 패스워드 가져와서 사용자가 입력한 패스워드와 일치 확인
        Member member = memberService.getMember(email);
        if (!passwordEncoder.matches(memberManagePasswordDto.getOldPassword(), member.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("current password match error");
        }

        //새 비밀번호와 비밀번호 확인 일치 체크
        if (!memberManagePasswordDto.getNewPassword().equals(memberManagePasswordDto.getPasswordCheck())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("password check error");
        }

        //비밀번호 변경 후 저장
        Member updatedMember = Member.updatePassword(member, memberManagePasswordDto, passwordEncoder);
        Member savedMember = memberService.modifyMember(updatedMember);

<<<<<<< Updated upstream
        return ResponseEntity.status(HttpStatus.OK).body(savedMember);
=======
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessDataResponse<Member>(savedMember));
>>>>>>> Stashed changes
    }

    //프로필 변경 화면 띄웠을 경우 이전 프로필 사진 보여주기 위해
    @GetMapping(value = "/manage/profile")
    public ResponseEntity<String> modifyProfile() {

        //사용자 email 얻기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        //해당 member 정보에서 S3에 저장된 파일 이름 가져와서 url 전송
        Member member = memberRepository.findByEmail(email);
        String profileImageUrl = profileImageBaseUrl + member.getProfileImage().getStoreFileName();

<<<<<<< Updated upstream
        return ResponseEntity.status(HttpStatus.OK).body(profileImageUrl);
=======
        UploadFile uploadFile = new UploadFile(member.getProfileImage().getUploadFileName(), profileImageUrl);

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessDataResponse<>(uploadFile));
>>>>>>> Stashed changes
    }

    //프로필 변경 요청시
    @PostMapping(value = "/manage/profile")
    public ResponseEntity<Object> modifyProfile(@RequestParam MultipartFile profileImageFile, @RequestParam String nickname) throws IOException {

        //프로필 사진이 없을 때
        if (profileImageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("profile image empty error");
        }

        //닉네임이 없을 때

        if (nickname.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("nickname empty error");
        }

        //중복된 닉네임일 경우
        if (memberService.checkDuplicateNickName(nickname)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate nickname");
        }

        //사용자 email 얻기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        //프로필 사진 저장을 위해 유저 정보 가져오기
        Member member = memberService.getMember(email);

        //S3 스토리지에 이전 파일 삭제 후 새로운 파일 저장, 저장된 파일 이름 반환
        UploadFile profileImage = s3Uploader.modifyProfileImage(profileImageFile, "profile-image", member.getProfileImage().getStoreFileName());


        //member 프로필 사진 이름 정보 수정
        Member updatedMember = Member.modifyProfile(member, profileImage, nickname);

        //DB에 저장
        Member savedMember = memberService.modifyMember(updatedMember);

<<<<<<< Updated upstream
        return ResponseEntity.status(HttpStatus.OK).body(savedMember);
=======
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessDataResponse<Member>(savedMember));
>>>>>>> Stashed changes
    }

    @PostMapping(value = "/manage/nickname")
    public ResponseEntity<String> modifyProfile(@RequestParam String nickname) {

        //중복된 닉네임일 경우
        if (memberService.checkDuplicateNickName(nickname)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate nickname");
        }
        //중복이 아닐 경우
<<<<<<< Updated upstream
        return ResponseEntity.status(HttpStatus.OK).body(nickname);
=======
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessDataResponse<String>(nickname));
>>>>>>> Stashed changes
    }

    @PostMapping(value = "/manage/delete")
    public ResponseEntity<String> deleteMember(@RequestParam String email) {

        Member byEmail = memberRepository.findByEmail(email);
        if (byEmail == null) { //해당 이메일의 회원정보를 찾을 수 없을 때
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no member error");
        }
        //사용자 email 얻기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String myEmail = ((UserDetails) principal).getUsername();

        if (!myEmail.equals(email)) { //삭제하려는 계정과 로그인된 계정이 다를 경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("not match email error");
        }
        memberService.deleteMember(byEmail);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
<<<<<<< Updated upstream
=======

    @PutMapping(value = "/change/password")
    public ResponseEntity<String> changePasswordWithToken(@Valid MemberPasswordDto memberPasswordDto, BindingResult bindingResult) {
        //빈칸있을 경우
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("blank error");
        }
        boolean passwordChangeCheck = memberService.changePasswordWithToken(memberPasswordDto,passwordEncoder);
        if(passwordChangeCheck){
            return ResponseEntity.status(HttpStatus.OK).body("password change success");
        }
        else{
            return ResponseEntity.status(HttpStatus.OK).body("password change failed");
        }
}

    @PostMapping(value = "/verify/password/token")
    public ResponseEntity<Boolean> verifyEmail(@RequestParam(required = true) String token){
        boolean result = emailService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
>>>>>>> Stashed changes
}