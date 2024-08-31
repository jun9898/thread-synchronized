package study.threadsynchronized.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import study.threadsynchronized.subject.SubjectName;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
}
