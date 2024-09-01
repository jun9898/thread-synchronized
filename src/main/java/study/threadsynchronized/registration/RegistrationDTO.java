package study.threadsynchronized.registration;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.threadsynchronized.subject.SubjectName;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RegistrationDTO {

	private String memberEmail;
	private SubjectName subjectName;

	public static RegistrationDTO create(String memberEmail, SubjectName subjectName) {
		return RegistrationDTO.builder()
				.memberEmail(memberEmail)
				.subjectName(subjectName)
				.build();
	}
}
