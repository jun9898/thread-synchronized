package study.threadsynchronized.registration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import study.threadsynchronized.member.Member;
import study.threadsynchronized.member.MemberRepository;
import study.threadsynchronized.structure.TaskQueue;
import study.threadsynchronized.subject.Subject;
import study.threadsynchronized.subject.SubjectName;
import study.threadsynchronized.subject.SubjectRepository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RegistrationService {

	private final RegistrationRepository registrationRepository;
	private final SubjectRepository subjectRepository;
	private final MemberRepository memberRepository;

	// TaskQueue: 수강신청 요청을 순차적으로 처리하기 위한 큐
	private final TaskQueue taskQueue = new TaskQueue();

	// ConcurrentHashMap: 과목별 현재 수강신청 인원을 추적하는 스레드 안전 맵
	// Key: 과목명, Value: 현재 수강신청 인원 (AtomicInteger)
	private final ConcurrentHashMap<String, AtomicInteger> subjectCurrentCount = new ConcurrentHashMap<>();

	// 수강신청 요청을 TaskQueue에 추가
	@Transactional
	public RegistrationResult register(RegistrationDTO registrationDTO) {
		CompletableFuture<RegistrationResult> future = new CompletableFuture<>();
		taskQueue.addTask(() -> {
			try {
				RegistrationResult result = registerSubject(registrationDTO);
				future.complete(result);
			} catch (Exception e) {
				future.completeExceptionally(e);
			}
		});
		return future.join();
	}

	// registerSubject 메서드에 동기화 블록 추가
	public RegistrationResult registerSubject(RegistrationDTO registrationDTO) {
		SubjectName subjectName = registrationDTO.getSubjectName();

		// 과목 정보 조회
		Subject subject = subjectRepository.findBySubjectName(subjectName);
		Member member = memberRepository.findByEmail(registrationDTO.getMemberEmail());
		if (subject == null) {
			return new RegistrationResult(false, "과목을 찾을 수 없습니다.");
		}

		AtomicInteger currentCount;
		synchronized (this) {
			// 과목의 현재 수강신청 인원 카운터 가져오기 (없으면 새로 생성)
			currentCount = subjectCurrentCount.computeIfAbsent(
				String.valueOf(subjectName), k -> new AtomicInteger(subject.getRegistration().size()));

			// 수강신청 인원 증가 및 최대 인원 체크
			if (currentCount.incrementAndGet() > subject.getMaximumStudent()) {
				currentCount.decrementAndGet(); // 초과 시 다시 감소
				return new RegistrationResult(false, "수강 신청 인원이 초과되었습니다.");
			}
		}

		try {
			// 수강신청 정보 저장
			Registration registration = Registration.of(member, subject);
			registrationRepository.save(registration);
			return new RegistrationResult(true, "수강 신청이 완료되었습니다.");
		} catch (Exception e) {
			synchronized (this) {
				currentCount.decrementAndGet(); // 저장 실패 시 인원 감소
			}
			throw e;
		}
	}

	@AllArgsConstructor
	public static class RegistrationResult {
		private final boolean success;
		private final String message;
	}
}
