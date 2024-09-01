package study.threadsynchronized.registration;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
public class RegistrationResult {

	@Getter
	private final boolean success;
	private final String message;

	public static RegistrationResult success() {
		return RegistrationResult.builder()
				.success(true)
				.message("수강 신청이 완료되었습니다.")
				.build();
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RegistrationResult that = (RegistrationResult) o;
		return success == that.success;
	}

	@Override
	public int hashCode() {
		return Objects.hash(success);
	}

}
