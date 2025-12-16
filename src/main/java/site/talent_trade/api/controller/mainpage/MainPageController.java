package site.talent_trade.api.controller.mainpage;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.talent_trade.api.domain.community.CommunitySortBy;
import site.talent_trade.api.domain.member.MemberSortBy;
import site.talent_trade.api.domain.member.Talent;
import site.talent_trade.api.dto.member.response.MemberListDTO;
import site.talent_trade.api.dto.member.response.MemberPageDTO;
import site.talent_trade.api.service.member.MainPageService;
import site.talent_trade.api.util.jwt.JwtProvider;
import site.talent_trade.api.util.response.ResponseDTO;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainPageController {

  private final MainPageService mainPageService;
  private final JwtProvider jwtProvider;

  @GetMapping("/recommend")
  public ResponseDTO<MemberListDTO> recommendMembers(HttpServletRequest httpServletRequest) {
    Long memberId = jwtProvider.validateToken(httpServletRequest);
    return mainPageService.recommendMembers(memberId);
  }

  @GetMapping("")
  public ResponseDTO<MemberPageDTO> getMainPage(HttpServletRequest httpServletRequest,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "talent", required = false) @Nullable Talent talent,
      @RequestParam(name = "memberSortBy", required = false) @Nullable MemberSortBy memberSortBy) {
    Long memberId = jwtProvider.validateToken(httpServletRequest);
    return mainPageService.getMainPageMembers(memberId, page, talent, memberSortBy);
  }

  //specification쓴거
  @GetMapping("/search-spec")
  public ResponseDTO<MemberPageDTO> searchMembers(HttpServletRequest httpServletRequest,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "memberSortBy", required = false) @Nullable MemberSortBy memberSortBy,
      @RequestParam(name = "keyword", required = false) @Nullable String query) {
    Long memberId = jwtProvider.validateToken(httpServletRequest);
    return mainPageService.searchSpecificationMembers(memberId, page, memberSortBy, query);
  }

    @GetMapping("/search")
    public ResponseDTO<MemberPageDTO> searchMembers(
            HttpServletRequest httpServletRequest,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "talent", required = false) @Nullable Talent talent,
            @RequestParam(name = "keyword", required = false) @Nullable String keyword,
            @RequestParam(name = "memberSortBy", required = false) @Nullable MemberSortBy memberSortBy
    ) {
        Long memberId = jwtProvider.validateToken(httpServletRequest);

        return mainPageService.searchMembers(
                memberId,
                page,
                talent,
                keyword,
                memberSortBy
        );
    }

}
