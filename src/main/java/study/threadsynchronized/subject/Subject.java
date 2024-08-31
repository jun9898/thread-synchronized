package study.threadsynchronized.subject;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
	private Long id;

	@Enumerated(EnumType.STRING)
	private SubjectName subjectName;

	@Builder.Default
	@OneToMany(mappedBy = "subject")
	private List<Registration> registration = new ArrayList<>();

	private Long maximumStudent;

}
