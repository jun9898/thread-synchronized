package study.threadsynchronized.registration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import study.threadsynchronized.structure.TaskQueue;
import study.threadsynchronized.subject.Subject;
import study.threadsynchronized.subject.SubjectRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegistrationService {

	private final RegistrationRepository registrationRepository;
	private final SubjectRepository subjectRepository;
	/*
	* work flow
	* 1. 학생 수강 신청 DTO를 수신해서 taskQueue에 add
	* 2. taskQueue에 있는 task를 execute (별도의 흐름으로 관리)
	* 3. 수강 신청 로직 수행
	* */

	private final TaskQueue taskQueue = new TaskQueue();

	@Transactional
	public RegistrationDTO register(RegistrationDTO registrationDTO) {
		taskQueue.addTask(() -> registerSubject(registrationDTO));
		return null;
	}

	private synchronized void registerSubject(RegistrationDTO registrationDTO) {
		Subject bySubjectName = subjectRepository.findBySubjectName(registrationDTO.getSubjectName());
		Long maximumStudent = bySubjectName.getMaximumStudent();
		Long currentStudent = (long)bySubjectName.getRegistration().size();
		if (currentStudent >= maximumStudent) {
			taskQueue.shutdown();
			throw new IllegalArgumentException("수강 신청 인원이 초과되었습니다.");
		}

		// 수강 신청 로직
	}
}
