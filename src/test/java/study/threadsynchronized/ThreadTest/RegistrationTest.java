package study.threadsynchronized.ThreadTest;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.threadsynchronized.registration.RegistrationDTO;
import study.threadsynchronized.registration.RegistrationResult;
import study.threadsynchronized.registration.RegistrationService;
import study.threadsynchronized.subject.Subject;
import study.threadsynchronized.subject.SubjectName;
import study.threadsynchronized.subject.SubjectRepository;

@SpringBootTest
public class RegistrationTest {

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private SubjectRepository subjectRepository;

	@Test
	public void singleRegistrationTest() {
		// given
		RegistrationDTO singleRegistration = RegistrationDTO.create("email0@example.com", SubjectName.COMPUTERSCIENCE);
		RegistrationResult expected = RegistrationResult.success();

		// when
		RegistrationResult register = registrationService.register(singleRegistration);

		// then
		assertThat(register).isEqualTo(expected);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void concurrentRegistrationsTest() throws InterruptedException, ExecutionException {
		// given
		List<RegistrationDTO> registrationRequests = IntStream.range(0, 50)
			.mapToObj(i -> RegistrationDTO.create("email" + i + "@example.com", SubjectName.COMPUTERSCIENCE))
			.toList();

		// when
		List<CompletableFuture<RegistrationResult>> futures = registrationRequests.stream()
			.map(request -> CompletableFuture.supplyAsync(() -> registrationService.register(request)))
			.toList();

		List<RegistrationResult> results = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
			.thenApply(v -> futures.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList()))
			.get();

		// then
		long successCount = results.stream().filter(RegistrationResult::isSuccess).count();
		long failureCount = results.stream().filter(result -> !result.isSuccess()).count();

		System.out.println("Success count: " + successCount);
		System.out.println("Failure count: " + failureCount);

		// Assert that there are no more successful registrations than the maximum allowed
		Subject subject = subjectRepository.findBySubjectName(SubjectName.COMPUTERSCIENCE);
		assertThat(successCount).isLessThanOrEqualTo(subject.getMaximumStudent());
	}
}
