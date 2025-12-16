package site.talent_trade.api.service.member;

import site.talent_trade.api.domain.community.CommunitySortBy;
import site.talent_trade.api.domain.member.MemberSortBy;
import site.talent_trade.api.domain.member.Talent;
import site.talent_trade.api.dto.member.response.MemberListDTO;
import site.talent_trade.api.dto.member.response.MemberPageDTO;
import site.talent_trade.api.util.response.ResponseDTO;

public interface MainPageService {

    /*내가 원하는 재능을 가지고 있는 무작위 유저 3명 추천*/
    ResponseDTO<MemberListDTO> recommendMembers(Long memberId);

    /*메인 페이지의 유저 목록 조회*/
    ResponseDTO<MemberPageDTO> getMainPageMembers(Long memberId, int page, Talent talent,
                                                  MemberSortBy memberSortBy);

    /*검색어로 회원 검색 - Specification 사용 */
    ResponseDTO<MemberPageDTO> searchSpecificationMembers(Long memberId, int page, MemberSortBy memberSortBy, String query);


    /* 검색어 + 재능 + 정렬 + 페이징으로 회원 검색 (Querydsl) */
    ResponseDTO<MemberPageDTO> searchMembers(Long memberId, int page, Talent talent, String keyword, MemberSortBy memberSortBy);
}
