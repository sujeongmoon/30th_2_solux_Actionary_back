package com.req2res.actionarybe.domain.study.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantNowStateRequestDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantNowStateResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantPrivateRequestDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantUserDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantUsersResponseDto;
import com.req2res.actionarybe.domain.study.dto.event.ChatMessageEvent;
import com.req2res.actionarybe.domain.study.dto.event.ChatMessageRequestEvent;
import com.req2res.actionarybe.domain.study.dto.event.ChatSenderInfo;
import com.req2res.actionarybe.domain.study.dto.event.NowStateChangedEvent;
import com.req2res.actionarybe.domain.study.dto.event.ParticipantJoinedEvent;
import com.req2res.actionarybe.domain.study.dto.event.ParticipantLeftEvent;
import com.req2res.actionarybe.domain.study.entity.Study;
import com.req2res.actionarybe.domain.study.entity.StudyParticipant;
import com.req2res.actionarybe.domain.study.repository.StudyParticipantRepository;
import com.req2res.actionarybe.domain.study.repository.StudyRepository;
import com.req2res.actionarybe.domain.studyTime.dto.StudyTimeTypeRequestDto;
import com.req2res.actionarybe.domain.studyTime.service.StudyTimeService;
import com.req2res.actionarybe.global.event.Event;
import com.req2res.actionarybe.global.event.EventType;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyParticipantService {

	private final StudyRepository studyRepository;
	private final StudyParticipantRepository studyParticipantRepository;
	private final MemberRepository memberRepository;

	private final StudyTimeService studyTimeService;

	private final RedisTemplate<String, String> redisTemplate;
	private final SimpMessagingTemplate messagingTemplate;

	public StudyParticipantResponseDto createStudyParticipantPublic(Member member, Long studyId) {

		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		if (studyParticipantRepository.countByStudyAndIsActiveTrue(study) >= study.getMemberLimit()) {
			throw new CustomException(ErrorCode.STUDY_CAPACITY_EXCEEDED);
		}

		if (studyParticipantRepository.existsByStudy_IdAndMember_IdAndIsActiveTrue(studyId, member.getId())) {
			throw new CustomException(ErrorCode.STUDY_PARTICIPANT_ALREADY_JOINED);
		}

		if (!study.getIsPublic()) {
			throw new CustomException(ErrorCode.STUDY_PARTICIPANT_PASSWORD_REQUIRED);
		}

		StudyParticipant studyParticipant = StudyParticipant.builder()
			.study(study)
			.member(member)
			.isActive(true)
			.lastStateChangedAt(LocalDateTime.now())
			.build();

		studyParticipantRepository.save(studyParticipant);

		messagingTemplate.convertAndSend(
			"/topic/studies/" + studyId,
			Event.builder()
				.type(EventType.PARTICIPANT_JOINED)
				.data(ParticipantJoinedEvent.builder()
					.studyParticipantId(studyParticipant.getId())
					.studyId(studyId)
					.userId(member.getId())
					.userNickname(member.getNickname())
					.profileImageUrl(member.getProfileImageUrl())
					.badgeId(member.getBadge().getId())
					.badgeImageUrl(member.getBadge().getImageUrl())
					.build())
				.build()
		);

		return StudyParticipantResponseDto.from(studyParticipant);
	}

	public StudyParticipantResponseDto createStudyParticipantPrivate(Member member, Long studyId,
		@Valid StudyParticipantPrivateRequestDto request) {

		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		if (studyParticipantRepository.countByStudyAndIsActiveTrue(study) >= study.getMemberLimit()) {
			throw new CustomException(ErrorCode.STUDY_CAPACITY_EXCEEDED);
		}

		if (studyParticipantRepository.existsByStudy_IdAndMember_IdAndIsActiveTrue(studyId, member.getId())) {
			throw new CustomException(ErrorCode.STUDY_PARTICIPANT_ALREADY_JOINED);
		}

		if (study.getIsPublic()) {
			throw new CustomException(ErrorCode.STUDY_PARTICIPANT_PASSWORD_UNREQUIRED);
		}

		PasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(request.getPassword(), study.getPassword())) {
			throw new CustomException(
				ErrorCode.STUDY_PARTICIPANT_PASSWORD_BAD_CREDENTIALS
			);
		}

		StudyParticipant studyParticipant = StudyParticipant.builder()
			.study(study)
			.member(member)
			.isActive(true)
			.lastStateChangedAt(LocalDateTime.now())
			.build();

		studyParticipantRepository.save(studyParticipant);

		return StudyParticipantResponseDto.from(studyParticipant);
	}

	public StudyParticipantUsersResponseDto getStudyParticipantUsers(Member member, Long studyId) {

		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		StudyParticipantUserDto me = studyParticipantRepository.findParticipantUserByStudyAndMemberAndIsActiveTrue(
				studyId, member.getId())
			.orElseThrow(() ->
				new CustomException(ErrorCode.STUDY_PARTICIPANT_NOT_JOINED)
			);

		List<StudyParticipantUserDto> participatingUsers = studyParticipantRepository.findParticipantUserByStudyAndIsActiveTrue(
			studyId);

		Map<Object, Object> nowStateMap = redisTemplate.opsForHash().entries("study:" + studyId + ":nowState");
		Map<Long, String> participantNowStates = new HashMap<>();
		for (Map.Entry<Object, Object> entry : nowStateMap.entrySet()) {
			Long key = Long.valueOf((String)entry.getKey());
			String value = (String)entry.getValue();
			participantNowStates.put(key, value);
		}

		return StudyParticipantUsersResponseDto.builder()
			.studyId(studyId)
			.me(me)
			.participatingUsers(participatingUsers)
			.participantNowStates(participantNowStates)
			.build();
	}

	public StudyParticipantNowStateResponseDto updateStudyParticipantNowState(
		@Valid StudyParticipantNowStateRequestDto request, Member member, Long studyId) {

		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		StudyParticipant studyParticipant = studyParticipantRepository.findByStudyAndMemberAndIsActiveTrue(
				study, member)
			.orElseThrow(() -> new CustomException(ErrorCode.STUDY_PARTICIPANT_NOT_JOINED));

		String redisKey = "study:" + studyId + ":nowState";

		redisTemplate.opsForHash()
			.put(
				redisKey,
				studyParticipant.getId().toString(),
				request.getNowState()
			);

		messagingTemplate.convertAndSend(
			"/topic/studies/" + studyId,
			Event.builder()
				.type(EventType.NOW_STATE_CHANGED)
				.data(NowStateChangedEvent.builder()
					.studyParticipantId(studyParticipant.getId())
					.studyId(studyId)
					.userId(member.getId())
					.nowState(request.getNowState())
					.build())
				.build()
		);

		return StudyParticipantNowStateResponseDto.builder()
			.studyId(studyId)
			.studyParticipantId(studyParticipant.getId())
			.userId(member.getId())
			.nowState(request.getNowState())
			.build();
	}

	@Transactional
	public void updateStudyParticipant(Member member, Long studyId, StudyTimeTypeRequestDto request) {
		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		StudyParticipant studyParticipant = studyParticipantRepository.findByStudyAndMemberAndIsActiveTrue(
				study, member)
			.orElseThrow(() -> new CustomException(ErrorCode.STUDY_PARTICIPANT_NOT_JOINED));

		studyTimeService.createStudyTime(request.getType(), studyParticipant);

		studyParticipant.updateIsActiveFalse(studyParticipant);

		// 말풍선삭제
		String redisKey = "study:" + studyId + ":nowState";
		redisTemplate.opsForHash().delete(
			redisKey,
			studyParticipant.getId().toString()
		);

		messagingTemplate.convertAndSend(
			"/topic/studies/" + studyId,
			Event.builder()
				.type(EventType.NOW_STATE_CHANGED)
				.data(ParticipantLeftEvent.builder()
					.studyId(studyId)
					.studyParticipantId(studyParticipant.getId())
					.build())
				.build()
		);

	}

	public void sendChatMessage(Long studyId, ChatMessageRequestEvent request) {

		Optional<ChatSenderInfo> chatSenderInfoOpt = studyParticipantRepository.findChatSenderInfo(studyId,
			request.getSenderId());

		if (chatSenderInfoOpt.isEmpty()) {
			messagingTemplate.convertAndSend(
				"/topic/studies/" + studyId,
				Event.builder()
					.type(EventType.NOT_STUDY_PARTICIPANT)
					.data("스터디 참여 정보를 찾을 수 없습니다.")
					.build()
			);
			return;
		}

		ChatSenderInfo chatSenderInfo = chatSenderInfoOpt.get();

		messagingTemplate.convertAndSend(
			"/topic/studies/" + studyId,
			Event.builder()
				.type(EventType.CHAT_MESSAGE)
				.data(ChatMessageEvent.builder()
					.studyParticipantId(chatSenderInfo.getStudyParticipantId())
					.studyId(studyId)
					.senderId(request.getSenderId())
					.senderNickname(chatSenderInfo.getSenderNickname())
					.badgeId(chatSenderInfo.getBadgeId())
					.badgeImageUrl(chatSenderInfo.getBadgeImageUrl())
					.chat(request.getChat())
					.createdAt(LocalDateTime.now())
					.build())
				.build()
		);

	}
}
