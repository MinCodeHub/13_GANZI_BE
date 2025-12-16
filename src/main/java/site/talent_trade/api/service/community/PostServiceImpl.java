package site.talent_trade.api.service.community;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.talent_trade.api.domain.Timestamp;
import site.talent_trade.api.domain.community.Comment;
import site.talent_trade.api.domain.community.Post;
import site.talent_trade.api.domain.community.PostSpecification;
import site.talent_trade.api.domain.community.CommunitySortBy;
import site.talent_trade.api.domain.member.Member;
import site.talent_trade.api.domain.notification.Notification;
import site.talent_trade.api.dto.commnuity.request.PostRequestDTO;
import site.talent_trade.api.dto.commnuity.response.CommentResponseDTO;
import site.talent_trade.api.dto.commnuity.response.PostDetailDTO;
import site.talent_trade.api.dto.commnuity.response.PostResponseDTO;
import site.talent_trade.api.repository.community.PostRepository;
import site.talent_trade.api.repository.member.MemberRepository;
import site.talent_trade.api.repository.notification.NotificationRepository;
import site.talent_trade.api.util.exception.CustomException;
import site.talent_trade.api.util.exception.ExceptionStatus;
import site.talent_trade.api.util.response.ResponseDTO;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NotificationRepository notificationRepository;


    //글 작성
    @Override
    public ResponseDTO<PostResponseDTO> savePost(PostRequestDTO postRequestDTO) {

        Member writer = memberRepository.findById(postRequestDTO.getWriterId())
                .orElseThrow(() -> new CustomException(ExceptionStatus.MEMBER_NOT_FOUND));

        Post newPost = Post.builder()
                .member(writer)
                .title(postRequestDTO.getTitle())
                .content(postRequestDTO.getContent())
                .hitCount(postRequestDTO.getHitCount())
                .timestamp(new Timestamp())
                .build();

        Post savedPost = postRepository.save(newPost);
        // 댓글 리스트가 null일 경우 빈 리스트로 처리하여 사이즈를 안전하게 호출
        int commentCount = (savedPost.getComments() != null) ? savedPost.getComments().size() : 0;

        //dto 반환
        PostResponseDTO postResponseDTO = PostResponseDTO.builder()
                .postId(savedPost.getId())
                .nickname(writer.getNickname())
                .title(savedPost.getTitle())
                .content(savedPost.getContent())
                .talent(writer.getMyTalent().name())
                .talentDetail(writer.getMyTalentDetail())
                .createdAt(savedPost.getTimestamp().getCreatedAt())
                .hitCount(savedPost.getHitCount())
                .commentCount(commentCount) // 댓글 개수 추가
                .gender(writer.getGender().name())
                .build();
        return new ResponseDTO<>(postResponseDTO, HttpStatus.OK);
    }

    //조회 및 검색 필터링
    @Override
    public ResponseDTO<List<PostResponseDTO>> getPostList(String talent, String keyword, CommunitySortBy communitySortBy) {
        // 기본 Specification 초기화
        Specification<Post> spec = Specification.where(PostSpecification.hasTalent(talent));

        // 필터링 조건 추가
        if (talent != null) {
            System.out.println("talent>>>>>" + talent);
            spec = spec.and(PostSpecification.hasTalent(talent));
        }
        if (keyword != null) {
            System.out.println("keyword>>>>>" + keyword);
            spec = spec.and(PostSpecification.containsKeyword(keyword));
        }
        System.out.println("sortBy>>>>>" + communitySortBy);
        // 정렬 조건 추가
        switch (communitySortBy) {

            case LATEST:
                spec = spec.and(PostSpecification.latestFirst());
                break;
            case HTI_COUNT:
                spec = spec.and(PostSpecification.hitCountHighest());
                break;
            case COMMENT_COUNT:
                spec = spec.and(PostSpecification.commentCountHighest());
                break;
            default:
                throw new IllegalArgumentException("Invalid sort type");
        }

        // 게시글 리스트 조회
        List<Post> posts = postRepository.findAll(spec);


        // PostResponseDTO로 변환
        List<PostResponseDTO> postResponseDTOs = posts.stream()
                .map(post -> {
                    // 댓글 리스트가 null일 경우 빈 리스트로 처리하여 사이즈를 안전하게 호출
                    int commentCount = (post.getComments() != null) ? post.getComments().size() : 0;

                    // 제목과 내용을 각각 최대 길이로 제한
                    String shortenedTitle = post.getTitle().length() > 18
                            ? post.getTitle().substring(0, 18) + "..."
                            : post.getTitle();
                    String shortenedContent = post.getContent().length() > 48
                            ? post.getContent().substring(0, 48) + "..."
                            : post.getContent();

                    return PostResponseDTO.builder()
                            .postId(post.getId())
                            .title(shortenedTitle) // 최대 길이 적용 제목
                            .content(shortenedContent) // 최대 길이 적용 내용
                            .hitCount(post.getHitCount())
                            .createdAt(post.getTimestamp().getCreatedAt())
                            .nickname(post.getMember().getNickname())
                            .talent(post.getMember().getMyTalent().name())
                            .talentDetail(post.getMember().getMyTalentDetail())
                            .commentCount(commentCount) // 댓글 개수 추가
                            .gender(post.getMember().getGender().name()) //성별 추가
                            .build();
                })
                .collect(Collectors.toList());

        // ResponseDTO로 반환
        return new ResponseDTO<>(postResponseDTOs, HttpStatus.OK);


    }

    //상세 조회 -> 조회수 하나씩 증가
    @Transactional
    @Override
    public ResponseDTO<PostDetailDTO> getPostDetail(Long postId, Long memberId) {


        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.POST_NOT_FOUND));

        List<Notification> notifications =
            notificationRepository.findUncheckedNotificationsByMemberIdAndPostId(memberId, postId);
        notifications.forEach(Notification::checkNotification);


        // 조회수 증가: 새로운 Post 객체 생성
        post.incrementHitCount();
        // 댓글 리스트가 null일 경우 빈 리스트로 처리하여 사이즈를 안전하게 호출
        int commentCount = (post.getComments() != null) ? post.getComments().size() : 0;
        log.info("Total comments: " + post.getComments().size());
        // 댓글 리스트가 null일 경우 빈 리스트로 처리
        List<Comment> comments = (post.getComments() != null) ? post.getComments() : new ArrayList<>();
        System.out.println("Comments list: " + comments);
        // 변경된 Post 저장
        postRepository.save(post);

        if (post.getComments() == null || post.getComments().isEmpty()) {
            log.warn("No comments found for post ID: " + post.getId());
        } else {
            log.info("Comments list: " + post.getComments());
        }

        // 댓글에 대해 알림 상태 업데이트하고 댓글 목록 가져오기
        List<CommentResponseDTO> commentResponseDTOs = post.getComments().stream()
                .map(comment -> {

                    // CommentResponseDTO 생성
                    CommentResponseDTO dto = CommentResponseDTO.builder()
                            .commentId(comment.getId())
                            .nickname(comment.getMember().getNickname())
                            .content(comment.getContent())
                            .talent(comment.getMember().getMyTalent().name())
                            .talentDetail(comment.getMember().getMyTalentDetail())
                            .createdAt(comment.getTimestamp().getCreatedAt())
                            .gender(comment.getMember().getGender().name())
                            .build();
                    //log.info("Created CommentResponseDTO: " + dto);  // Log the DTO
                    return dto;
                })
                .collect(Collectors.toList());

        // PostResponseDTO 객체 생성
        PostResponseDTO postResponseDTO = PostResponseDTO.builder()
                .postId(post.getId())
                .nickname(post.getMember().getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .talent(post.getMember().getMyTalent().name())
                .talentDetail(post.getMember().getMyTalentDetail())
                .createdAt(post.getTimestamp().getCreatedAt())
                .hitCount(post.getHitCount())
                .commentCount(commentCount) // 댓글 개수 추가
                .gender(post.getMember().getGender().name())
                .build();
        // PostDetailDTO 객체 생성
        PostDetailDTO postDetailDTO = PostDetailDTO.builder()
                .post(postResponseDTO) // 게시글 정보
                .comments(commentResponseDTOs) // 댓글 목록
                .build();

        // ResponseDTO로 반환
        return new ResponseDTO<>(postDetailDTO, HttpStatus.OK);
    }

    //마이페이지 - 내가 쓴 게시물 가져오기
    @Override
    public ResponseDTO<List<PostResponseDTO>> findByMemberId(Long memberId) {

        List<Post> myPosts = postRepository.findByMemberId(memberId);
        // 조회 결과를 PostResponseDTO로 변환
        List<PostResponseDTO> postResponseDTOs = myPosts.stream()
                .map(post ->

                        PostResponseDTO.builder()
                                .postId(post.getId())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .createdAt(post.getTimestamp().getCreatedAt())
                                .nickname(post.getMember().getNickname())
                                .hitCount(post.getHitCount())
                                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                                .build())
                .collect(Collectors.toList());

        // 응답 반환
        return new ResponseDTO<>(postResponseDTOs, HttpStatus.OK);
    }
}
