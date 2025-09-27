package com.coffee.service;

import com.coffee.constant.Role;
import com.coffee.entity.Member;
import com.coffee.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service // 서비스 역할을 하며, 주로 로직 처리에 활용되는 자바 클래스입니다.
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;



    public Member findByEmail(String email) {

    return memberRepository.findByEmail(email);
    }

    public void insert(Member bean) {
        // 사용자 '역할(Role)'과 '등록 일자(Regdate)'는 여기서 넣습니다.
        bean.setRole(Role.USER);
        bean.setRegdate(LocalDate.now());
        // 주의) Repsitory에서 인서트 작업은 save() 메소드를 사용합니다.
        memberRepository.save(bean);
    }

    public Optional<Member> findByMemberId(Long memberId) {
        return this.memberRepository.findById(memberId);
    }
}
