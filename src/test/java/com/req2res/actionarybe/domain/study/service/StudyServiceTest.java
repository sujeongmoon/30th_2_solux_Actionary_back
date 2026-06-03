package com.req2res.actionarybe.domain.study.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.lenient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.req2res.actionarybe.domain.image.service.ImageService;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.study.dto.JanusSessionRequestDto;
import com.req2res.actionarybe.domain.study.dto.JanusSessionResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyDetailResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyListResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyRequestDto;
import com.req2res.actionarybe.domain.study.dto.StudyResponseDto;
import com.req2res.actionarybe.domain.study.entity.Category;
import com.req2res.actionarybe.domain.study.entity.Study;
import com.req2res.actionarybe.domain.study.repository.StudyLikeRepository;
import com.req2res.actionarybe.domain.study.repository.StudyParticipantRepository;
import com.req2res.actionarybe.domain.study.repository.StudyRepository;
import com.req2res.actionarybe.domain.studyTime.entity.Type;
import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeManualRepository;
import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeRepository;
import com.req2res.actionarybe.global.config.JanusClient;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock JanusClient janusClient;
    @Mock StudyRepository studyRepository;
    @Mock StudyParticipantRepository studyParticipantRepository;
    @Mock StudyLikeRepository studyLikeRepository;
    @Mock StudyTimeRepository studyTimeRepository;
    @Mock StudyTimeManualRepository studyTimeManualRepository;
    @Mock ImageService imageService;

    @InjectMocks StudyService studyService;

    private static final String DEFAULT_COVER_URL = "https://default.cover/image.jpg";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(studyService, "defaultCoverUrl", DEFAULT_COVER_URL);
    }

    // ──────────────────────────────────────────────────────────────
    // 테스트용 픽스처 팩토리
    // ──────────────────────────────────────────────────────────────

    private Member mockMember(long id) {
        Member member = mock(Member.class);
        lenient().when(member.getId()).thenReturn(id);
        return member;
    }

    private Study mockStudy(long id, Member creator) {
        Study study = mock(Study.class);
        lenient().when(study.getId()).thenReturn(id);
        lenient().when(study.getName()).thenReturn("테스트 스터디");
        lenient().when(study.getCoverImage()).thenReturn("cover.jpg");
        lenient().when(study.getCategory()).thenReturn(Category.LANGUAGE);
        lenient().when(study.getDescription()).thenReturn("설명");
        lenient().when(study.getLongDescription()).thenReturn("긴 설명");
        lenient().when(study.getMemberLimit()).thenReturn(10);
        lenient().when(study.getIsPublic()).thenReturn(true);
        lenient().when(study.getCreator()).thenReturn(creator);
        return study;
    }

    private StudyRequestDto mockRequest(boolean isPublic, String password) {
        StudyRequestDto request = mock(StudyRequestDto.class);
        lenient().when(request.getStudyName()).thenReturn("테스트 스터디");
        lenient().when(request.getCategory()).thenReturn(Category.LANGUAGE);
        lenient().when(request.getDescription()).thenReturn("설명");
        lenient().when(request.getLongDescription()).thenReturn("긴 설명");
        lenient().when(request.getMemberLimit()).thenReturn(10);
        lenient().when(request.getIsPublic()).thenReturn(isPublic);
        lenient().when(request.getPassword()).thenReturn(password);
        return request;
    }

    // ══════════════════════════════════════════════════════════════
    // createStudy
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("createStudy")
    class CreateStudy {

        @Test
        @DisplayName("커버 이미지 없으면 기본 이미지 URL 사용")
        void 커버이미지_없으면_기본이미지_사용() {
            Member member = mockMember(1L);
            StudyRequestDto request = mockRequest(true, null);

            given(studyRepository.save(any(Study.class))).willAnswer(inv -> inv.getArgument(0));

            StudyResponseDto result = studyService.createStudy(member, request, null);

            assertThat(result.getCoverImage()).isEqualTo(DEFAULT_COVER_URL);
        }

        @Test
        @DisplayName("커버 이미지 있으면 imageService로 저장한 URL 사용")
        void 커버이미지_있으면_저장된_URL_사용() {
            Member member = mockMember(1L);
            StudyRequestDto request = mockRequest(true, null);
            MultipartFile file = mock(MultipartFile.class);
            given(file.isEmpty()).willReturn(false);
            given(imageService.saveImage(file)).willReturn("uploaded.jpg");
            given(studyRepository.save(any(Study.class))).willAnswer(inv -> inv.getArgument(0));

            StudyResponseDto result = studyService.createStudy(member, request, file);

            assertThat(result.getCoverImage()).isEqualTo("uploaded.jpg");
        }

        @Test
        @DisplayName("비밀번호 있으면 BCrypt 암호화해서 저장")
        void 비밀번호_있으면_암호화() {
            Member member = mockMember(1L);
            StudyRequestDto request = mockRequest(false, "123456");
            given(studyRepository.save(any(Study.class))).willAnswer(inv -> {
                Study saved = inv.getArgument(0);
                // password 필드가 암호화되었는지 확인: 평문이 아닌 값이어야 함
                assertThat(saved.getPassword()).isNotEqualTo("123456");
                assertThat(saved.getPassword()).isNotBlank();
                return saved;
            });

            studyService.createStudy(member, request, null);

            then(studyRepository).should().save(any(Study.class));
        }

        @Test
        @DisplayName("Janus 생성 실패 시 스터디 삭제 후 STUDY_CREATE_ERROR 예외")
        void Janus_생성_실패시_스터디_삭제() {
            Member member = mockMember(1L);
            StudyRequestDto request = mockRequest(true, null);
            given(studyRepository.save(any(Study.class))).willAnswer(inv -> inv.getArgument(0));
            willThrow(new RuntimeException("Janus down"))
                .given(janusClient).createStudyRoom(anyLong(), anyInt());

            assertThatThrownBy(() -> studyService.createStudy(member, request, null))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.STUDY_CREATE_ERROR));

            then(studyRepository).should().delete(any(Study.class));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // createJanusRoomId
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("createJanusRoomId")
    class CreateJanusRoomId {

        @Test
        @DisplayName("정상 요청이면 studyId 반환")
        void 정상요청_studyId_반환() {
            Member member = mockMember(1L);
            Study study = mockStudy(10L, member);
            JanusSessionRequestDto request = mock(JanusSessionRequestDto.class);
            given(request.getStudyId()).willReturn(10L);
            given(studyRepository.findById(10L)).willReturn(Optional.of(study));

            JanusSessionResponseDto result = studyService.createJanusRoomId(request);

            assertThat(result.getStudyId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("스터디 없으면 STUDY_NOT_FIND 예외")
        void 스터디_없으면_예외() {
            JanusSessionRequestDto request = mock(JanusSessionRequestDto.class);
            given(request.getStudyId()).willReturn(99L);
            given(studyRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> studyService.createJanusRoomId(request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.STUDY_NOT_FIND));
        }

        @Test
        @DisplayName("Janus 실패 시 STUDY_CREATE_ERROR 예외")
        void Janus_실패시_예외() {
            Member member = mockMember(1L);
            Study study = mockStudy(10L, member);
            JanusSessionRequestDto request = mock(JanusSessionRequestDto.class);
            given(request.getStudyId()).willReturn(10L);
            given(studyRepository.findById(10L)).willReturn(Optional.of(study));
            willThrow(new RuntimeException()).given(janusClient).createStudyRoom(anyLong(), anyInt());

            assertThatThrownBy(() -> studyService.createJanusRoomId(request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.STUDY_CREATE_ERROR));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // deleteStudy
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("deleteStudy")
    class DeleteStudy {

        @Test
        @DisplayName("방장이고 참여자 없으면 삭제 성공")
        void 정상_삭제() {
            Member member = mockMember(1L);
            Study study = mockStudy(10L, member);
            given(studyRepository.findById(10L)).willReturn(Optional.of(study));
            given(study.getCreator()).willReturn(member);
            given(studyParticipantRepository.countByStudyAndIsActiveTrue(study)).willReturn(0);

            studyService.deleteStudy(member, 10L);

            then(studyRepository).should().delete(study);
        }

        @Test
        @DisplayName("스터디 없으면 STUDY_NOT_FIND 예외")
        void 스터디_없으면_예외() {
            Member member = mockMember(1L);
            given(studyRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> studyService.deleteStudy(member, 99L))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.STUDY_NOT_FIND));
        }

        @Test
        @DisplayName("방장이 아니면 STUDY_NOT_MATCH_MEMBER 예외")
        void 방장아니면_예외() {
            Member creator = mockMember(1L);
            Member other = mockMember(2L);
            Study study = mockStudy(10L, creator);
            given(studyRepository.findById(10L)).willReturn(Optional.of(study));
            given(study.getCreator()).willReturn(creator);

            assertThatThrownBy(() -> studyService.deleteStudy(other, 10L))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.STUDY_NOT_MATCH_MEMBER));
        }

        @Test
        @DisplayName("활성 참여자 있으면 STUDY_HAVE_USER 예외")
        void 참여자_있으면_예외() {
            Member member = mockMember(1L);
            Study study = mockStudy(10L, member);
            given(studyRepository.findById(10L)).willReturn(Optional.of(study));
            given(study.getCreator()).willReturn(member);
            given(studyParticipantRepository.countByStudyAndIsActiveTrue(study)).willReturn(3);

            assertThatThrownBy(() -> studyService.deleteStudy(member, 10L))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.STUDY_HAVE_USER));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // updateStudy
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("updateStudy")
    class UpdateStudy {

        @Test
        @DisplayName("새 이미지 없으면 기존 이미지 유지")
        void 새이미지_없으면_기존이미지_유지() {
            Member member = mockMember(1L);
            Study study = mockStudy(10L, member);
            StudyRequestDto request = mockRequest(true, null);
            given(studyRepository.findById(10L)).willReturn(Optional.of(study));
            given(study.getCreator()).willReturn(member);

            studyService.updateStudy(member, request, 10L, null);

            then(study).should().updateStudy(eq(request), eq(member), eq("cover.jpg"));
        }

        @Test
        @DisplayName("새 이미지 있으면 imageService로 저장한 URL로 갱신")
        void 새이미지_있으면_갱신() {
            Member member = mockMember(1L);
            Study study = mockStudy(10L, member);
            StudyRequestDto request = mockRequest(true, null);
            MultipartFile file = mock(MultipartFile.class);
            given(file.isEmpty()).willReturn(false);
            given(studyRepository.findById(10L)).willReturn(Optional.of(study));
            given(study.getCreator()).willReturn(member);
            given(imageService.saveImage(file)).willReturn("new.jpg");

            studyService.updateStudy(member, request, 10L, file);

            then(study).should().updateStudy(eq(request), eq(member), eq("new.jpg"));
        }

        @Test
        @DisplayName("스터디 없으면 STUDY_NOT_FIND 예외")
        void 스터디_없으면_예외() {
            Member member = mockMember(1L);
            StudyRequestDto request = mockRequest(true, null);
            given(studyRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> studyService.updateStudy(member, request, 99L, null))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.STUDY_NOT_FIND));
        }

        @Test
        @DisplayName("방장이 아니면 STUDY_NOT_MATCH_MEMBER 예외")
        void 방장아니면_예외() {
            Member creator = mockMember(1L);
            Member other = mockMember(2L);
            Study study = mockStudy(10L, creator);
            StudyRequestDto request = mockRequest(true, null);
            given(studyRepository.findById(10L)).willReturn(Optional.of(study));
            given(study.getCreator()).willReturn(creator);

            assertThatThrownBy(() -> studyService.updateStudy(other, request, 10L, null))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.STUDY_NOT_MATCH_MEMBER));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // getStudyDetail
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getStudyDetail")
    class GetStudyDetail {

        @Test
        @DisplayName("스터디 존재하면 상세 정보 반환")
        void 상세정보_반환() {
            Member member = mockMember(1L);
            Study study = mockStudy(10L, member);
            given(studyRepository.findById(10L)).willReturn(Optional.of(study));
            given(studyParticipantRepository.countByStudyAndIsActiveTrue(study)).willReturn(5);
            given(studyLikeRepository.existsByStudyAndMember(study, member)).willReturn(true);
            given(study.getCreator()).willReturn(member);

            StudyDetailResponseDto result = studyService.getStudyDetail(member, 10L);

            assertThat(result.getStudyId()).isEqualTo(10L);
            assertThat(result.getMemberNow()).isEqualTo(5);
            assertThat(result.getIsStudyLike()).isTrue();
            assertThat(result.getIsStudyOwner()).isTrue();
        }

        @Test
        @DisplayName("스터디 없으면 STUDY_NOT_FIND 예외")
        void 스터디_없으면_예외() {
            Member member = mockMember(1L);
            given(studyRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> studyService.getStudyDetail(member, 99L))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.STUDY_NOT_FIND));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // getStudyList
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getStudyList")
    class GetStudyList {

        @Test
        @DisplayName("public + 카테고리 있으면 findByIsPublicAndCategory 호출")
        void public_카테고리_필터() {
            Page<Study> page = new PageImpl<>(List.of(), PageRequest.of(0, 8), 0);
            given(studyRepository.findByIsPublicAndCategory(eq(true), eq(Category.LANGUAGE), any()))
                .willReturn(page);

            StudyListResponseDto result = studyService.getStudyList("public", Category.LANGUAGE, 0);

            assertThat(result.getIsPublic()).isTrue();
            assertThat(result.getCategory()).isEqualTo(Category.LANGUAGE);
        }

        @Test
        @DisplayName("private + 카테고리 없으면 isPublic=false로 findByIsPublic 호출")
        void private_카테고리없음() {
            Page<Study> page = new PageImpl<>(List.of(), PageRequest.of(0, 8), 0);
            // primitive boolean 파라미터이므로 anyBoolean() 사용
            given(studyRepository.findByIsPublic(anyBoolean(), any())).willReturn(page);

            StudyListResponseDto result = studyService.getStudyList("private", null, 0);

            assertThat(result.getIsPublic()).isFalse();
            assertThat(result.getCategory()).isNull();
            then(studyRepository).should().findByIsPublic(eq(false), any());
        }

        @Test
        @DisplayName("public + 카테고리 없으면 isPublic=true로 findByIsPublic 호출")
        void public_카테고리없음() {
            Page<Study> page = new PageImpl<>(List.of(), PageRequest.of(0, 8), 0);
            given(studyRepository.findByIsPublic(anyBoolean(), any())).willReturn(page);

            StudyListResponseDto result = studyService.getStudyList("public", null, 0);

            assertThat(result.getIsPublic()).isTrue();
            then(studyRepository).should().findByIsPublic(eq(true), any());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // findUsersStudiedToday
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findUsersStudiedToday")
    class FindUsersStudiedToday {

        @Test
        @DisplayName("자동+수동 userId 합쳐서 중복 제거 후 반환")
        void 중복제거_후_반환() {
            given(studyTimeRepository.findDistinctUserIdsStudiedToday(
                any(LocalDateTime.class), any(LocalDateTime.class), eq(Type.STUDY))
            ).willReturn(List.of(1L, 2L, 3L));
            given(studyTimeManualRepository.findDistinctUserIdsByManualDate(any(LocalDate.class)))
                .willReturn(List.of(2L, 4L));

            List<Long> result = studyService.findUsersStudiedToday();

            assertThat(result).containsExactlyInAnyOrder(1L, 2L, 3L, 4L);
            assertThat(result).doesNotHaveDuplicates();
        }
    }

    // ══════════════════════════════════════════════════════════════
    // getYesterdayTotalStudySeconds
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getYesterdayTotalStudySeconds")
    class GetYesterdayTotalStudySeconds {

        @Test
        @DisplayName("자동 + 수동 공부 시간 합산 반환")
        void 자동_수동_합산() {
            given(studyTimeRepository.sumStudySecondsTodayByUserId(
                eq(1L), any(), any(), eq(Type.STUDY))
            ).willReturn(3600);
            given(studyTimeManualRepository.sumManualStudySecondsByUserIdAndDate(
                eq(1L), any(LocalDate.class))
            ).willReturn(1800);

            int result = studyService.getYesterdayTotalStudySeconds(1L);

            assertThat(result).isEqualTo(5400);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // buildYesterdaySummaryText
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("buildYesterdaySummaryText")
    class BuildYesterdaySummaryText {

        private void givenTotalSeconds(int auto, int manual) {
            given(studyTimeRepository.sumStudySecondsTodayByUserId(
                eq(1L), any(), any(), eq(Type.STUDY))
            ).willReturn(auto);
            given(studyTimeManualRepository.sumManualStudySecondsByUserIdAndDate(
                eq(1L), any(LocalDate.class))
            ).willReturn(manual);
        }

        @Test
        @DisplayName("공부 기록 없으면 휴식 메시지 반환")
        void 공부기록_없음() {
            givenTotalSeconds(0, 0);

            String result = studyService.buildYesterdaySummaryText(1L);

            assertThat(result).isEqualTo("어제는 공부 기록이 없었어요 😴");
        }

        @Test
        @DisplayName("1시간 미만이면 분 단위 메시지 반환")
        void 시간미만_분단위() {
            givenTotalSeconds(1800, 0); // 30분

            String result = studyService.buildYesterdaySummaryText(1L);

            assertThat(result).isEqualTo("어제 총 30분 공부했어요 👏");
        }

        @Test
        @DisplayName("1시간 이상이면 시간+분 메시지 반환")
        void 시간이상_시간분() {
            givenTotalSeconds(5400, 0); // 1시간 30분

            String result = studyService.buildYesterdaySummaryText(1L);

            assertThat(result).isEqualTo("어제 총 1시간 30분 공부했어요 👏");
        }

        @Test
        @DisplayName("정확히 1시간이면 0분 표시")
        void 정확히_1시간() {
            givenTotalSeconds(3600, 0); // 1시간 0분

            String result = studyService.buildYesterdaySummaryText(1L);

            assertThat(result).isEqualTo("어제 총 1시간 0분 공부했어요 👏");
        }
    }
}
