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

public interface RegistrationService {
	public RegistrationResult register(RegistrationDTO registrationDTO);
}
