package site.talent_trade.api.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import site.talent_trade.api.domain.Timestamp;
import site.talent_trade.api.domain.community.Post;
import site.talent_trade.api.domain.member.Gender;
import site.talent_trade.api.domain.member.Member;
import site.talent_trade.api.domain.member.Talent;
import site.talent_trade.api.repository.community.PostRepository;
import site.talent_trade.api.repository.member.MemberRepository;
import site.talent_trade.api.service.community.PostService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.util.StopWatch;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PostHitCountConcurrencyTest {
    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 동시에_100명이_조회하면_hitCount는_100이어야한다_조회수_증가_시_update문_사용() throws InterruptedException {
        //given

        Member member = memberRepository.save(
                Member.builder()
                        .nickname("tester")
                        .myTalent(Talent.IT)
                        .myTalentDetail("백엔드")
                        .gender(Gender.MALE)
                        .build()
        );

        Post newPost = Post.builder()
                .member(member)
                .title("제목")
                .content("내용")
                .hitCount(0)
                .timestamp(new Timestamp())
                .build();

        Post post = postRepository.save(newPost);

        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    postService.getPostDetail(post.getId(), member.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        stopWatch.stop();

        //then

        Post result = postRepository.findById(post.getId()).orElseThrow();
        System.out.println("--- Update 쿼리 방식 결과 ---");
        System.out.println("수행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
        System.out.println("최종 조회수: " + result.getHitCount());

        assertThat(result.getHitCount()).isEqualTo(threadCount);
    }

    @Test
    void 동시에_100명이_조회하면_hitCount는_100이어야한다_조회수_증가_시_비관적락_사용() throws InterruptedException {
        //given

        Member member = memberRepository.save(
                Member.builder()
                        .nickname("tester")
                        .myTalent(Talent.IT)
                        .myTalentDetail("백엔드")
                        .gender(Gender.MALE)
                        .build()
        );

        Post newPost = Post.builder()
                .member(member)
                .title("제목")
                .content("내용")
                .hitCount(0)
                .timestamp(new Timestamp())
                .build();

        Post post = postRepository.save(newPost);

        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    postService.getPostDetail(post.getId(), member.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        stopWatch.stop(); // 시간 측정 종료
        //then

        Post result = postRepository.findById(post.getId()).orElseThrow();

        System.out.println("--- 비관적 락 방식 결과 ---");
        System.out.println("수행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
        System.out.println("최종 조회수: " + result.getHitCount());

        assertThat(result.getHitCount()).isEqualTo(threadCount);
    }


}
