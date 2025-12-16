package site.talent_trade.api.controller.community;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import site.talent_trade.api.domain.community.CommunitySortBy;
import site.talent_trade.api.dto.commnuity.request.PostRequestDTO;
import site.talent_trade.api.dto.commnuity.response.PostDetailDTO;
import site.talent_trade.api.dto.commnuity.response.PostResponseDTO;
import site.talent_trade.api.service.community.PostService;
import site.talent_trade.api.util.jwt.JwtProvider;
import site.talent_trade.api.util.response.ResponseDTO;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Slf4j
public class PostController {

    private final PostService postService;

    private final JwtProvider jwtProvider;


    // 게시글 리스트 조회 (jwt 인증 필요 없음)
    @GetMapping("/get")
    public ResponseDTO<List<PostResponseDTO>> getPostList(@RequestParam(value = "talent", required = false) String talent,
                                                          @RequestParam(value = "keyword", required = false) String keyword,
                                                          @RequestParam(value = "communitySortBy", defaultValue = "LATEST") CommunitySortBy communitySortBy) {
        log.info("talent: "+talent);
        log.info("sortBy: "+ communitySortBy);
        // 게시글 검색 및 필터링
        return postService.getPostList(talent, keyword, communitySortBy);
    }

    // 게시글 작성(jwt 인증 필요)
    @PostMapping("/create")
    public ResponseDTO<PostResponseDTO> createPost(@RequestBody PostRequestDTO postRequestDTO, HttpServletRequest request) {
        // JWT 토큰을 통해 사용자 인증
        Long memberId = jwtProvider.validateToken(request);

        // 게시물 작성
        postRequestDTO.setWriterId(memberId);  // 토큰에서 추출한 memberId로 작성자 설정

        return postService.savePost(postRequestDTO);
    }

    // 게시글 상세 조회 (댓글 포함)
    @GetMapping("/detail/{postId}")
    public ResponseDTO<PostDetailDTO> getPostDetail(HttpServletRequest request, @PathVariable Long postId) {

        Long memberId = jwtProvider.validateToken(request);

        return postService.getPostDetail(postId,memberId);
    }

    //마이페이지 -> 내가 쓴 게시물 가져오기(댓글 개수, 제목, 날짜)
    @GetMapping("/mine")
    public ResponseDTO<List<PostResponseDTO>> getMyPostList(HttpServletRequest request) {
        // JWT 토큰을 통해 사용자 인증
        Long memberId = jwtProvider.validateToken(request);

        return postService.findByMemberId(memberId);
    }
}
