package study.threadsynchronized.subject;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.threadsynchronized.registration.Registration;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Subject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private SubjectName subjectName;

	@Builder.Default
	@OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
	private List<Registration> registration = new ArrayList<>();

	private Long maximumStudent;

	public static Subject create(SubjectName subjectName, Long maximumStudent) {
		return Subject.builder()
				.subjectName(subjectName)
				.maximumStudent(maximumStudent)
				.build();
	}

	public void addRegistration(Registration registration) {
		this.registration.add(registration);
	}

}
