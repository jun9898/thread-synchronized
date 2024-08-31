package study.threadsynchronized.member;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import study.threadsynchronized.registration.Registration;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	@Id
	private Long id;
	@Builder.Default
	@OneToMany(mappedBy = "member")
	private List<Registration> registration = new ArrayList<>();

}
