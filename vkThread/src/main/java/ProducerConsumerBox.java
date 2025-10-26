import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProducerConsumerBox {

    private final Object monitor = new Object();
    private String item;

    public String take() {
        String result;
        do {
            synchronized (monitor) {
                if (this.item == null) {
                    try {
                        monitor.notifyAll();
                        monitor.wait(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    result = this.item;
                    this.item = null;
                    monitor.notifyAll();
                    return result;
                }
            }
        } while (!Thread.currentThread().isInterrupted());
        return null;
    }

    public void put(String item) {
        do {
            synchronized (monitor) {
                if (this.item != null) {
                    try {
                        monitor.notifyAll();
                        monitor.wait(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    this.item = item;
                    monitor.notifyAll();
                    return;
                }
            }
        } while (!Thread.currentThread().isInterrupted());
    }

    public static void main(String... args) throws InterruptedException {
        ProducerConsumerBox box = new ProducerConsumerBox();
        ExecutorService service = new ThreadPoolExecutor(
                5,
                1000,
                20,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(true),
                r -> new Thread(r, "ProducerConsumerBox-")
        );
        for (int i = 1; i < 101; i++) {
            int finalI = i;
            service.execute(() -> box.put(String.valueOf(finalI)));
        }
        System.out.println("Put all values!");
        ConcurrentHashMap<Integer, String> set = new ConcurrentHashMap<>();
        for (int i = 1; i < 101; i++) {
            int finalI = i;
            service.execute(() -> {
                String boxValue = box.take();
                set.put(finalI, boxValue);
            });
        }
        System.out.println("Take all values!");
        service.shutdown();
        if (service.awaitTermination(20, TimeUnit.SECONDS))
            System.out.println("All threads shutdown!");
        System.out.println("All threads is terminate!");
        if (set.size() != 100) throw new RuntimeException("Bullshit! " + set.size());
//        ReentrantLock lock = new ReentrantLock(true);
//        lock.lock();
//        ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock(true);
//        rwlock.readLock();
//        rwlock.writeLock();

//        CountDownLatch cdl = new CountDownLatch(5);

//        CyclicBarrier cb = new CyclicBarrier(4, () -> System.out.println("!"));

        Semaphore s = new Semaphore(2, true);

    }

}
