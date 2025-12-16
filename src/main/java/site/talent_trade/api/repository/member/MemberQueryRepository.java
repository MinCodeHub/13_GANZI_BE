package site.talent_trade.api.repository.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.talent_trade.api.domain.member.Member;
import site.talent_trade.api.domain.member.MemberSortBy;
import site.talent_trade.api.domain.member.Talent;

public interface MemberQueryRepository {
    Page<Member> searchMembers(
            Long excludeMemberId,
            Talent talent,
            String keyword,
            MemberSortBy memberSortBy,
            Pageable pageable
    );
}
