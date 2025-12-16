package site.talent_trade.api.service.community;

import site.talent_trade.api.domain.community.CommunitySortBy;
import site.talent_trade.api.dto.commnuity.request.PostRequestDTO;
import site.talent_trade.api.dto.commnuity.response.PostDetailDTO;
import site.talent_trade.api.dto.commnuity.response.PostResponseDTO;
import site.talent_trade.api.util.response.ResponseDTO;

import java.util.List;

/*  필터링 -> 세부분야, 최신순(포스트 생성순)
    필터링 -> 세부분야, 댓글 개수순
    필터링 -> 세부분야, 조회순
    필터링 -> 전체, 최신순 (default)
    필터링 -> 전체, 리뷰개수순
    필터링 -> 전체, 평점 높은 순
    제목으로 검색 */

public interface PostService {

    //글 작성
    ResponseDTO<PostResponseDTO> savePost(PostRequestDTO postRequestDTO);

    //다중 필터 및 검색
    ResponseDTO<List<PostResponseDTO>> getPostList(String talent,String keyword, CommunitySortBy communitySortBy);

    //글 조회 -> 조회할 때 hitCount + 1 해줘야 함
    ResponseDTO<PostDetailDTO> getPostDetail(Long postId,Long memberId);

    //내가 작성한 글 조회
    ResponseDTO<List<PostResponseDTO>> findByMemberId(Long memberId);

}
