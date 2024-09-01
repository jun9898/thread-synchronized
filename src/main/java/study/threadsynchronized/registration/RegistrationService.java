package study.threadsynchronized.registration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import study.threadsynchronized.member.Member;
import study.threadsynchronized.member.MemberRepository;
import study.threadsynchronized.structure.TaskQueue;
import study.threadsynchronized.subject.Subject;
import study.threadsynchronized.subject.SubjectName;
import study.threadsynchronized.subject.SubjectRepository;

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
		SubjectName subjectName = registrationDTO.getSubjectName();

		// 과목 정보 조회
		Subject subject = subjectRepository.findBySubjectName(subjectName);
		Member member = memberRepository.findByEmail(registrationDTO.getMemberEmail());
		// CompletableFuture 내부에서 실행되는 코드가 원래의 트랜잭션 컨텍스트와 별도로 비동기적으로 실행되기 때문에 사용할 수 없음
		// 즉 Task 내부가 아닌 외부에서 값을 조회하고 CompletableFuture 내부에서 사용해야함
		Hibernate.initialize(subject.getRegistration());
		Hibernate.initialize(member.getRegistration());

		taskQueue.addTask(() -> {
			try {
				RegistrationResult result = registerSubject(subject, member);
				future.complete(result);
			} catch (Exception e) {
				future.completeExceptionally(e);
			}
		});
		return future.join();
	}

	// registerSubject 메서드에 동기화 블록 추가
	private RegistrationResult registerSubject(Subject subject, Member member) {
		if (subject == null) {
			return new RegistrationResult(false, "과목을 찾을 수 없습니다.");
		}

		// synchronized 블록에 들어가면 trancactional의 session이 종료되어서 lazy loading이 안됨
		// legeno 사건
		/* 해결방법
		* 1. Hibernate.initialize(subject.getRegistration());로 명시적으로 session이 열려있을때 가져오기
		* 	하지만 이 방법은 CompletableFuture 내에서 실행되는 코드가 원래의 트랜잭션 컨텍스트와 별도로 비동기적으로 실행되기 때문에 사용할 수 없음
		* 2. Fetch Join 사용
		* 	이 방법이 사실 제일 쉽고 간편하다. 근데 이 방법은 지금 entity 연관관계가 OneToMany라서 적합하지 않을것같음.
		* 3. CompletableFuture 바깥에서 값을 조회하고 CompletableFuture 내부에서 사용
		* 	이 방법으로 한번 가보자
		* 	일단 성공!!!!!!!!!!!!!
		 * */
		AtomicInteger currentCount;
		synchronized (this) {
			// 과목의 현재 수강신청 인원 카운터 가져오기 (없으면 새로 생성)
			currentCount = subjectCurrentCount.computeIfAbsent(
				String.valueOf(subject.getSubjectName()), k -> new AtomicInteger(subject.getRegistration().size()));

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

}
