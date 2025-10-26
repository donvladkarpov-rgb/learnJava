import java.util.Random;

public class VkThreadPoolExecutorTest {

    public static void main(String... args) {
        Random r = new Random();
        VkThreadPoolExecutor executor = new VkThreadPoolExecutor(5);
        for(int i = 0; i < 50; i++) {
            int intI = i;
            executor.execute(()-> {
                System.out.println("Start job #" + intI);
                try {
                    Thread.sleep(r.nextInt(1000, 3001));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    System.out.println("Stop job #" + intI);
                }
            });
        }
        executor.shutdown();
        try {
            executor.execute(() -> System.out.println("By the way..."));
        } catch (IllegalStateException e) {
            System.out.println("Add jobs after shutdown unavailable!");
        }
        if (!executor.awaitTermination()) {
            System.out.println("Bullshit!");
        } else {
            System.out.println("All right!");
        }
    }

}
