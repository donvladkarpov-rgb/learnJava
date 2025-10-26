import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class VkThreadPoolExecutor {

    private final Object monitor = new Object();
    private final List<VkThreadPoolThread> threadPool = new ArrayList<>();
    private final LinkedList<Runnable> jobs = new LinkedList<>();
    private volatile boolean shutdownState = false;
    private CountDownLatch cdl;

    public VkThreadPoolExecutor(int poolSize) {
        synchronized (monitor) {
            for (int i = 0; i < poolSize; i++) {
                VkThreadPoolThread t = new VkThreadPoolThread();
                threadPool.add(t);
                t.start();
            }
            cdl = new CountDownLatch(poolSize);
        }
    }

    public void execute(Runnable job) throws IllegalStateException {
        synchronized (monitor) {
            if (shutdownState) throw new IllegalStateException();
            jobs.add(job);
            monitor.notifyAll();
        }
    }

    public void shutdown() {
        synchronized (monitor) {
            shutdownState = true;
        }
    }

    private boolean getShutdownState() {
        synchronized (monitor) {
            return shutdownState;
        }
    }

    private boolean isJobsEmpty() {
        synchronized (monitor) {
            return jobs.isEmpty();
        }
    }

    public boolean isTerminated() {
        boolean terminated = true;
        for (VkThreadPoolThread t : threadPool) terminated = t.terminated && terminated;
        return terminated;
    }

    public boolean awaitTermination() {
        try {
            cdl.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return isTerminated();
    }

    private class VkThreadPoolThread extends Thread {

        public volatile boolean terminated = false;

        @Override
        public void run() {
            try {
                while (!(isInterrupted() || (VkThreadPoolExecutor.this.getShutdownState() && VkThreadPoolExecutor.this.isJobsEmpty()))) {
                    Runnable job;
                    synchronized (monitor) {
                        job = jobs.pollLast();
                    }
                    if (job != null) {
                        try {
                            job.run();
                        } catch (Exception e) {
                            System.out.println("Error executing job!");
                        }
                    } else {
                        synchronized (monitor) {
                            try {
                                monitor.wait();
                            } catch (InterruptedException e) {
                                interrupt();
                            }
                        }
                    }
                }
            } finally {
                terminated = true;
                cdl.countDown();
            }
        }
    }

}
