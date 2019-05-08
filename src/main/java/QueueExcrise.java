import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueExcrise {

    public static void main(String[] args) throws InterruptedException {
            BlockingQueue<String> queue = new ArrayBlockingQueue(10);

            Producer producer1 = new Producer(queue);
            Producer producer2 = new Producer(queue);
            Producer producer3 = new Producer(queue);
            Consumer consumer = new Consumer(queue);

            ExecutorService service = Executors.newCachedThreadPool();

            service.execute(producer1);
            service.execute(producer2);
            service.execute(producer3);
            service.execute(consumer);

            Thread.sleep(10 * 1000);
            producer1.stop();
            producer2.stop();
            producer3.stop();

            Thread.sleep(2000);
            // 退出Executor
            service.shutdown();


    }
}
class Producer implements Runnable {
    private volatile boolean      isRunning               = true;
    private BlockingQueue<String> queue;
    private static AtomicInteger  count                   = new AtomicInteger();
    private static final int      DEFAULT_RANGE_FOR_SLEEP = 1000;

    public Producer(BlockingQueue queue){
        this.queue = queue;
    }

    public void run(){
        String data = null;
        Random r = new Random();
        System.out.println("启动生产者线程");
        try{
            while(isRunning){
                System.out.println("正在生产数据.....");
                Thread.sleep(r.nextInt(DEFAULT_RANGE_FOR_SLEEP));

                data = "data:" + count.incrementAndGet();
                System.out.println("将数据：" + data + "放入队列...");
                if (!queue.offer(data, 2, TimeUnit.SECONDS)) {
                    System.out.println("放入数据失败：" + data);
                }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        finally{
            System.out.println("退出生产者线程！");
        }
    }

    public void stop(){
        isRunning = false;
    }
}

class Consumer implements Runnable{
    private BlockingQueue<String> queue;
    private static final int      DEFAULT_RANGE_FOR_SLEEP = 1000;

    public Consumer(BlockingQueue<String> queue){
        this.queue = queue;
    }

    public void run(){
        System.out.println("启动消费者线程：");
        Random r = new Random();
        boolean isRunning = true;
        try{
            while(isRunning){
                System.out.println("正从队列获取数据...");
                String data = queue.poll(2,TimeUnit.SECONDS);
                if(null != data){
                    System.out.println("拿到数据：" + data);
                    System.out.println("正在消费数据：" + data);
                    Thread.sleep(r.nextInt(DEFAULT_RANGE_FOR_SLEEP));
                }else{
                    isRunning = false;
                }
            }
        }catch(InterruptedException e){
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }finally{
            System.out.println("退出消费者线程！");
        }
    }
}
