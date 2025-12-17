//package site.talent_trade.api.util.dummy;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import site.talent_trade.api.domain.member.Gender;
//import site.talent_trade.api.domain.member.Member;
//import site.talent_trade.api.domain.member.Talent;
//import site.talent_trade.api.domain.profile.MeetingType;
//import site.talent_trade.api.domain.profile.Profile;
//import site.talent_trade.api.repository.member.MemberRepository;
//
//@Component
//@org.springframework.context.annotation.Profile("local-profile")
//@RequiredArgsConstructor
//public class DummyMemberDataLoader implements CommandLineRunner {
//    private final MemberRepository memberRepository;
//    private static final Random random = new Random();
//
//    private static final int MEMBER_COUNT = 100000;
//    private static final String[] REGIONS = {"Seoul", "Busan", "Incheon", "Daegu", "Gwangju", "Jeju", "Daejeon", "Ulsan"}; // 지역 다양화
//    private static final int BATCH_SIZE = 1000; // 배치 삽입을 위한 사이즈 (선택 사항)
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        if (memberRepository.count() > 0) {
//            return;
//        }
//
//        Talent[] talents = Talent.values();
//        Gender[] genders = Gender.values();
//        MeetingType[] meetingTypes = MeetingType.values();
//
//        //배치 삽입 리스트
//        List<Member> membersToSave = new ArrayList<>();
//
//        System.out.println("⏳ Member / Profile 더미 데이터 생성 시작...");
//
//        for (int i = 1; i <= MEMBER_COUNT; i++) {
//
//            Talent myTalent = talents[i % talents.length];
//            Talent wishTalent = talents[(i + 2) % talents.length];
//            Gender gender = genders[i % genders.length];
//
//            // 닉네임 길이를 10만 건에 맞춰 조정 (unique하도록 i를 포함)
//            String base = myTalent.name().toLowerCase();
//            String nickname = (base.length() > 3 ? base.substring(0, 3) : base) + i;
//            if (nickname.length() > 20) nickname = nickname.substring(0, 20); // DB 닉네임 길이 제한에 따라 조정
//
//            Member member = Member.builder()
//                    .email("user" + i + "@test.com")
//                    .password("password")
//                    .name("u" + i)
//                    .nickname(nickname)
//                    // 전화번호도 10만 건에 맞게 포맷 조정
//                    .phone("010-" + String.format("%04d", i / 1000) + "-" + String.format("%04d", i % 1000))
//                    .birth(LocalDate.of(
//                            1990 + random.nextInt(10),
//                            random.nextInt(12) + 1,
//                            random.nextInt(28) + 1
//                    ))
//                    .gender(gender)
//                    .build();
//
//            // Member 기본 정보 설정
//            member.updateMember(
//                    member.getNickname(),
//                    myTalent,
//                    myTalent + " 전문가",
//                    wishTalent,
//                    "안녕하세요. " + myTalent + " 재능을 가지고 있습니다."
//            );
//
//            // Profile 설정
//            Profile profile = member.getProfile();
//            profile.updateProfile(
//                    myTalent + " 관련 경험을 보유하고 있습니다.",
//                    "경력 " + (random.nextInt(10) + 1) + "년",
//                    "https://portfolio.example.com/" + i,
//                    REGIONS[random.nextInt(REGIONS.length)],
//                    meetingTypes[random.nextInt(meetingTypes.length)],
//                    genders[random.nextInt(genders.length)]
//            );
//
//            // 리뷰 / 점수 / 거래 횟수 분포
//            int reviewCnt = random.nextInt(50);
//            for (int r = 0; r < reviewCnt; r++) {
//                profile.updateScore(random.nextInt(5) + 1);
//            }
//
//            // 거래 횟수도 10만 건 데이터 분포에 맞게 조정될 수 있도록 유지
//            int tradeCnt = random.nextInt(30);
//            for (int t = 0; t < tradeCnt; t++) {
//                profile.increaseTradeCnt();
//            }
//
//            // 배치 삽입 로직: 리스트에 멤버 추가
//            membersToSave.add(member);
//
//            // BATCH_SIZE 단위로 저장
//            if (i % BATCH_SIZE == 0 || i == MEMBER_COUNT) {
//                memberRepository.saveAll(membersToSave);
//                membersToSave.clear();
//                System.out.printf("  > %d건 저장 완료\n", i);
//            }
//        }
//
//        // 혹시 리스트에 남은 데이터가 있다면 저장
//        if (!membersToSave.isEmpty()) {
//            memberRepository.saveAll(membersToSave);
//        }
//
//        System.out.println("🔥 Member / Profile 더미 데이터 생성 완료 (총 " + MEMBER_COUNT + "건)");
//    }
//}