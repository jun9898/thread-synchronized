package study.threadsynchronized.registration;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.threadsynchronized.member.Member;
import study.threadsynchronized.subject.Subject;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Registration {

	@Id
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "subject_id")
	private Subject subject;

	public static Registration of(Member member, Subject subject) {
		Registration registration = Registration.builder()
			.member(member)
			.subject(subject)
			.build();
		member.addRegistration(registration);
		subject.addRegistration(registration);
		return registration;
	}
}
