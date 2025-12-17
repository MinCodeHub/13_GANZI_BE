package site.talent_trade.api.repository.community;


import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.talent_trade.api.domain.community.Post;
import site.talent_trade.api.domain.member.Talent;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    //멤버 아이디로 내가 쓴 게시물 조회하기
    List<Post> findByMemberId(Long memberId);

    //update 쿼리로 조회수 직접 업데이트 하기
    @Modifying
    @Query("update Post p set p.hitCount = p.hitCount + 1 where p.id = :postId")
    void increaseHit(@Param("postId") Long postId);

    //비관적 락: select 시점에 락이 걸림 -> 트랜잭션 종료 전까지 다른 쓰기 접근 금지
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Post p where p.id = :id")
    Post findByIdForUpdate(@Param("id") Long postId);

}