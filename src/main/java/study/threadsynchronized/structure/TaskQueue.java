package study.threadsynchronized.structure;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue {

	private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
	private final ExecutorService executor = Executors.newSingleThreadExecutor(); // 작업을 처리할 스레드 풀

	public void addTask(Runnable task) {
		Boolean result = taskQueue.add(task);
		processTasks(); // 작업이 추가될 때마다 처리 시작
	}

	private void processTasks() {
		executor.submit(() -> {
			while (!taskQueue.isEmpty()) {
				try {
					Runnable task = taskQueue.take();
					task.run();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
	}

	public void shutdown() {
		executor.shutdown();
		while (!taskQueue.isEmpty()) {
			try {
				Runnable task = taskQueue.take();
				task.run();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}	}
}
