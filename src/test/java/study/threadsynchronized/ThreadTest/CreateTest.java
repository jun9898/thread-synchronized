package study.threadsynchronized.ThreadTest;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import study.threadsynchronized.member.Member;
import study.threadsynchronized.member.MemberRepository;
import study.threadsynchronized.registration.RegistrationRepository;
import study.threadsynchronized.subject.Subject;
import study.threadsynchronized.subject.SubjectName;
import study.threadsynchronized.subject.SubjectRepository;

@SpringBootTest
public class CreateTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private RegistrationRepository registrationRepository;

	@Test
	@Rollback(false)
	public void memberCreate() {
		// given
		List<Member> members = IntStream.range(0, 100)
			.mapToObj(i -> Member.create("email" + i + "@example.com"))
			.toList();
		// when
		memberRepository.saveAll(members);

		// then
	}

	@Test
	@Rollback(false)
	public void subjectCreate() {
		// given
		List<SubjectName> subjectNames = List.of(
			SubjectName.BIOLOGY, SubjectName.COMPUTERSCIENCE, SubjectName.CHEMISTRY, SubjectName.PHYSICS
		);

		List<Subject> subjects = subjectNames.stream()
			.map(subjectName -> Subject.create(subjectName, 30L))
			.toList();

		// when
		subjectRepository.saveAll(subjects);

		// then
	}
}

