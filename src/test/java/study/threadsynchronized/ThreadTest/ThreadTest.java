package study.threadsynchronized.ThreadTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ThreadTest {

	private int counter = 0; // 공유자원

	@Test
	public void testThreadSafety() throws InterruptedException {
		int numberOfThreads = 100;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads); // Thread 선언

		IntStream.range(0, numberOfThreads).forEach(i -> {
			new Thread(() -> {
				IntStream.range(0, 1000).forEach(j -> incrementCounter());
				countDownLatch.countDown();
			}).start();
		});

		countDownLatch.await();
		System.out.println("Final counter value : " + counter); // 값이 100000이 나옴
	}

	// synchronized를 사용함으로
	// 현재 사용중인 Thread를 제외한 다른 Thread에서 데이터에 접근할 수 없음
	private void incrementCounter() {
		System.out.println("응애에용");
		synchronized (this) {
			counter++;
		}
	}


	private final ReentrantLock lock = new ReentrantLock(); // ReentrantLock 객체 선언

	@Test
	public void testThreadSafetyReentrantLock() throws InterruptedException {
		int numberOfThreads = 100;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads); // Thread 선언

		IntStream.range(0, numberOfThreads).forEach(i -> {
			new Thread(() -> {
				IntStream.range(0, 1000).forEach(j -> incrementCounter());
				countDownLatch.countDown();
			}).start();
		});

		countDownLatch.await();
		System.out.println("Final counter value : " + counter); // 값이 100000이 나옴
	}

	private void ReentrantLockincrementCounter() {
		lock.lock(); // Lock을 획득
		try {
			counter++;
		} finally {
			lock.unlock(); // Lock을 해제
		}
	}
}
