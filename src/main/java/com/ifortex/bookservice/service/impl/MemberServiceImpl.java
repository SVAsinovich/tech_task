package com.ifortex.bookservice.service.impl;

import com.ifortex.bookservice.model.Book;
import com.ifortex.bookservice.model.Member;
import com.ifortex.bookservice.repository.MemberRepository;
import com.ifortex.bookservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public static final String GENRE = "Romance";
    public static final int MEMBERSHIP_DATE = 2023;

    @Override
    public Member findMember() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .flatMap(
                        member -> member.getBorrowedBooks().stream()
                                .filter(book -> book.getGenres().contains(GENRE))
                )
                .min(Comparator.comparing(Book::getPublicationDate))
                .stream()
                .flatMap(
                        oldestBook -> members.stream()
                                .filter(m -> m.getBorrowedBooks().contains(oldestBook))
                )
                .max(Comparator.comparing(Member::getMembershipDate))
                .orElse(null);
    }

    @Override
    public List<Member> findMembers() {
        return memberRepository.findAll().stream()
                .filter(member -> member.getMembershipDate().getYear() == MEMBERSHIP_DATE)
                .filter(member -> member.getBorrowedBooks().isEmpty())
                .toList();
    }
}
