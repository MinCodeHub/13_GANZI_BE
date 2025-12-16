package site.talent_trade.api.repository.member;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import site.talent_trade.api.domain.member.Member;
import site.talent_trade.api.domain.member.MemberSortBy;
import site.talent_trade.api.domain.member.QMember;
import site.talent_trade.api.domain.member.Talent;
import site.talent_trade.api.domain.profile.QProfile;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Member> searchMembers(
            Long excludeMemberId,
            Talent talent,
            String keyword,
            MemberSortBy memberSortBy,
            Pageable pageable
    ) {
        //Q클래스 선언
        QMember member = QMember.member;
        QProfile profile = QProfile.profile;

        //select  * from member + 프로필 조인 + fetchJoin() n+1방지
        List<Member> content = queryFactory
                .selectFrom(member)
                .leftJoin(member.profile, profile).fetchJoin()
                .where(
                        excludeMember(excludeMemberId, member),
                        hasTalent(talent, member),
                        searchKeyword(keyword, member, profile)
                )
                .orderBy(orderBy(memberSortBy, member, profile))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        //전체 개수 조회
        Long total = queryFactory
                .select(member.count())
                .from(member)
                .leftJoin(member.profile, profile)
                .where(
                        excludeMember(excludeMemberId, member),
                        hasTalent(talent, member),
                        searchKeyword(keyword, member, profile)
                )
                .fetchOne();

        long totalCount = total == null ? 0L : total;

        return new PageImpl<>(content, pageable, totalCount);
    }

    //Querydsl 조건 메서드

    private BooleanExpression excludeMember(Long memberId, QMember member) {
        if (memberId == null) return null;
        return member.id.ne(memberId);
    }

    private BooleanExpression searchKeyword(
            String keyword, QMember member, QProfile profile) {

        if (keyword == null || keyword.isBlank()) return null;

        return member.nickname.contains(keyword)
                .or(member.myTalent.stringValue().contains(keyword))
                .or(member.myTalentDetail.contains(keyword))
                .or(member.myComment.contains(keyword))
                .or(member.wishTalent.stringValue().contains(keyword))
                .or(profile.talentIntro.stringValue().contains(keyword))
                .or(profile.experienceIntro.stringValue().contains(keyword))
                .or(profile.region.contains(keyword));
    }

    private BooleanExpression hasTalent(Talent talent, QMember member) {
        if (talent == null) return null;
        return member.myTalent.eq(talent);
    }


    private OrderSpecifier<?> orderBy(
            MemberSortBy memberSortBy, QMember member, QProfile profile) {

        if (memberSortBy == null) {
            return member.timestamp.createdAt.desc();
        }

        return switch (memberSortBy) {
            case REVIEW -> profile.reviewCnt.desc();
            case SCORE  -> profile.scoreAvg.desc();
            default     -> member.timestamp.createdAt.desc();
        };
    }
}
