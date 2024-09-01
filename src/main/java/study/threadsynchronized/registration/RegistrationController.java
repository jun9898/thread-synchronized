package study.threadsynchronized.registration;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RegistrationController {

	/*
	work flow
	1. 학생 수강 신청
	2. 학생 ID와 수강 신청할 과목 RequestBody로 받음
	3. registrationService.register() 호출
	* */

	private final RegistrationService registrationService;

	@PostMapping("/registration")
	public RegistrationResult classRegistration(@RequestBody RegistrationDTO registrationDTO) {
		return registrationService.register(registrationDTO);
	}

}
