package study.threadsynchronized.structure;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskQueue {
	private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final AtomicBoolean isProcessing = new AtomicBoolean(false);

	public void addTask(Runnable task) {
		taskQueue.add(task);
		processTasks();
	}

	private void processTasks() {
		if (isProcessing.compareAndSet(false, true)) {
			executor.submit(() -> {
				try {
					while (!taskQueue.isEmpty()) {
						Runnable task = taskQueue.take();
						try {
							task.run();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					isProcessing.set(false);
					if (!taskQueue.isEmpty()) {
						processTasks(); // 작업이 남아있다면 다시 처리 시작
					}
				}
			});
		}
	}

	public void shutdown() {
		executor.shutdown();
		while (!executor.isTerminated()) {
			try {
				Runnable task = taskQueue.poll();
				if (task != null) {
					task.run();
				} else {
					Thread.sleep(100); // 작업이 없으면 잠시 대기
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}
