package study.threadsynchronized.member;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.threadsynchronized.registration.Registration;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	@Builder.Default
	@OneToMany(mappedBy = "member")
	private List<Registration> registration = new ArrayList<>();

	public static Member create(String email) {
		return Member.builder()
				.email(email)
				.build();
	}

	public void addRegistration(Registration registration) {
		this.registration.add(registration);
	}

}
