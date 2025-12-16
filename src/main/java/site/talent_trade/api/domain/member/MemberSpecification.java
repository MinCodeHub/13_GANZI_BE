package site.talent_trade.api.domain.member;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpecification {

  public static Specification<Member> orderBy(MemberSortBy memberSortBy) {
    if (memberSortBy == null) {
      return null;
    }
    return switch (memberSortBy) {
      case REVIEW -> orderByReviewCnt();
      case SCORE -> orderByScoreAvg();
      default -> orderByCreatedAt();
    };
  }
  private static Specification<Member> orderByCreatedAt() {
    return (root, query, criteriaBuilder) -> {
      if (query == null) {
        return null;
      }
      query.orderBy(criteriaBuilder.desc(root.get("timestamp").get("createdAt")));
      return query.getRestriction();
    };
  }

  private static Specification<Member> orderByReviewCnt() {
    return (root, query, criteriaBuilder) -> {
      if (query == null) {
        return null;
      }
      var profileJoin = root.join("profile", JoinType.LEFT);
      query.orderBy(criteriaBuilder.desc(profileJoin.get("reviewCnt")));
      return query.getRestriction();
    };
  }

  private static Specification<Member> orderByScoreAvg() {
    return (root, query, criteriaBuilder) -> {
      if (query == null) {
        return null;
      }
      var profileJoin = root.join("profile", JoinType.LEFT);
      query.orderBy(criteriaBuilder.desc(profileJoin.get("scoreAvg")));
      return query.getRestriction();
    };
  }

  public static Specification<Member> hasTalent(Talent talent) {
    return (root, query, criteriaBuilder) -> {
      if (query == null) {
        return null;
      }
      if (talent == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("myTalent"), talent);
    };
  }

  /*키워드로 검색*/
  public static Specification<Member> searchByKeyword(String keyword) {
    return (root, query, criteriaBuilder) -> {
      if (query == null) {
        return null;
      }
      if (keyword == null) {
        return criteriaBuilder.conjunction();
      }
      var profileJoin = root.join("profile", JoinType.LEFT);
      return criteriaBuilder.or(
          criteriaBuilder.like(root.get("nickname"), "%" + keyword + "%"),
          criteriaBuilder.like(root.get("myTalent"), "%" + keyword + "%"),
          criteriaBuilder.like(root.get("myTalentDetail"), "%" + keyword + "%"),
          criteriaBuilder.like(root.get("myComment"), "%" + keyword + "%"),
          criteriaBuilder.like(root.get("wishTalent"), "%" + keyword + "%"),
          criteriaBuilder.like(profileJoin.get("talentIntro"), "%" + keyword + "%"),
          criteriaBuilder.like(profileJoin.get("experienceIntro"), "%" + keyword + "%"),
          criteriaBuilder.like(profileJoin.get("region"), "%" + keyword + "%"));
    };
  }

  /*검색하는 유저 본인은 결과에서 제외*/
  public static Specification<Member> excludeMember(Long memberId) {
    return (root, query, criteriaBuilder) -> {
      if (query == null) {
        return null;
      }
      return criteriaBuilder.notEqual(root.get("id"), memberId);
    };
  }
}
