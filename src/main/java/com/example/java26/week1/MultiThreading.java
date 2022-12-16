package com.example.java26.week1;

/**
 *  CAS: compare and swap
 *
 *  CAS(obj reference address, field reference, expected value, new value)
 *
 *  if(obj.field == expected value) {
 *      obj.field = new value;
 *      return true
 *  } else {
 *      return false;
 *  }
 *
 *  Atomic Library
 */

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1. volatile int id + synchronized increment()
 * 2. synchronized increment() + synchronized get()
 * 3.
 */
class IncrementIdGenerator {
    private AtomicInteger id;

    public IncrementIdGenerator() {
        this(0);
    }

    public IncrementIdGenerator(int val) {
        this.id = new AtomicInteger(val);
    }

    public void increment() {
        id.incrementAndGet();
    }

    public int get() {
        return id.get();
    }
}
/**
 *  synchronized
 *      1. unfair lock
 *      2. obj -> waiting
 *         notifyAll()
 *         notify()
 *      3. try synchronized(obj)
 *      4. func1() {
 *              synchronized() {
 *
 *              }
 *         }
 *
 *         func2() {
 *             release
 *         }
 *
 *
 *  ReentrantLock {
 *      state = 0 : unlock
 *      state >= 1 : locked
 *      current thread owner : thread reference
 *      condition : waiting list
 *
 *      lock() / unlock()
 *      tryLock(time)
 * }
 *
 * AbstractQueuedSynchronizer : Queue(LinkedList)
 *                                          <-  N5   CAS(tail, expected Node(T3), N5)
 *      Node(T1) <-> Node(T2) <-> Node(T3)  <-  N4   CAS(tail, expected Node(T3), N4)
 *      head                      tail      <-  N6   CAS(tail, expected Node(T3), N6)
 *
 *
 * ReentrantLock lock = new ReentrantLock();
 * lock.newCondition();
 * lock.newCondition();
 *     *     *     *     *     *     *     *     *     *     *     *     *
 *  BlockingQueue
 *     producer -> [][][][][][][][][][] -> consumer
 *
 *    consumer poll() {
 *        lock();
 *        while(queue is empty) {
 *            current thread / consumer await();
 *        }
 *        take(e)
 *        notify producer
 *        unlock();
 *    }
 *
 *
 *    producer offer(e) {
 *        lock();
 *        while(queue is full) {
 *            current thread / producer await();
 *        }
 *        add(e) into queue
 *        notify consumer
 *        unlock();
 *    }
 */
class MyBlockingQueue<T> {

    private final Object[] queue;
    private int size;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition fullWaitingList = lock.newCondition();
    private final Condition emptyWaitingList = lock.newCondition();
    private int startIndex;
    private int endIndex;

    public MyBlockingQueue(int size) {
        this.queue = new Object[size];
    }

    public T poll() throws Exception {
        lock.lock();
        try {
            while(size == 0) {
                emptyWaitingList.await();
            }
            Object res = queue[startIndex++];
            if(startIndex == queue.length) {
                startIndex = 0;
            }
            size--;
            fullWaitingList.signal();
            return (T)res;
        } finally {
            lock.unlock();
        }
    }

    public void offer(T ele) throws Exception {
        lock.lock();
        try {
            while(size == queue.length) {
                fullWaitingList.await();
            }
            queue[endIndex++] = ele;
            if(endIndex == queue.length) {
                endIndex = 0;
            }
            emptyWaitingList.signal();
            size++;
        } finally {
            lock.unlock();
        }
    }
}
/**
 *  Thread Pool (diff thread pools + how to decide thread number /count)
 *  ThreadPoolExecutor(
 *      int corePoolSize,
 *      int maxPoolSize,
 *      int idleTime,
 *      TimeUnit unit,
 *      BlockQueue queue,
 *      ThreadFactory factory
 *  )
 *
 *     producer -> ([][][][][][] blocking queue  ->  worker thread) Thread pool
 *
 *     1. fixedThreadPool : core == max
 *     2. cachedThreadPool: core != max
 *     3. scheduledThreadPool: delayedWorkQueue
 *       thread1
 *       thread2(wait 3s) -> [task1(3s)][task3(5s)][task5(t3)]
 *       thread3
 *
 *       time wheel
 *       [0][1][2][..][59] minute
 *             |
 *            p1
 *
 *       [0][1][2][..][59] second
 *        |
 *       p2
 *
 *  ForkJoinPool (stealing algorithm)
 *
 *     [][][][][][][][][][][] -> T1 [x1][x2][x3][x4]
 *                               T2 [][][][]
 *    *    *    *    *    *    *    *    *    *    *    *    *    *    *
 * thread pool thread number
 *    CPU / service / calculator based task :  core number + 1
 *    (IO + service) task : n * ( 1 / (1 - IO percentage)) + 1
 *    50%   50%
 */
class ThreadPoolTest {
    private final static ExecutorService pool = Executors.newCachedThreadPool();

    public static void get() throws Exception {
        Future<Integer> f = pool.submit(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 5;
        });
        System.out.println(f.isDone());
        int res = f.get();
        System.out.println(res);
    }

    public static void main(String[] args) throws Exception {
        get();
        get();
    }
}

/**
 *  deadlock
 *
 *      T1 : holding lock A {
 *          try to lock B {
 *
 *          }
 *      }
 *
 *      T2 : holding lock B {
 *          try to lock A {
 *
 *          }
 *      }
 *
 *   1. timeout
 *   2. lock in order
 *   3. look up table
 */

/**
 *  semaphore (permit)
 *      example
 *          permit = 4
 *          T1 acquire permit = 3
 *          T2 acquire permit = 2
 *          T2 release permit = 3
 *  countdownlatch
 */
class CountDownLatchDemo {
    private static final CountDownLatch latch = new CountDownLatch(2);
    public synchronized static void func1() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        latch.countDown();
        System.out.println(Thread.currentThread() + ", count down");
    }
    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" this is t1");
        });
        Thread t2 = new Thread(() -> func1());
        Thread t3 = new Thread(() -> func1());
        t1.start();
        t2.start();
        t3.start();

        t1.join();
    }
}
/**
 *  cyclicbarrier
 *    *    *    *    *    *    *    *    *    *    *    *    *
 *
 *      read lock + write lock == share lock + exclusive lock
 *
 *      share lock only blocks ex lock
 *      ex lock blocks share lock + ex clock
 *
 *       *    *    *    *    *    *    *    *    *    *    *
 *
 *   abstract queued synchronized
 *   how to impl reentrant lock
 *   x86 / hardware
 *   how does g1 work
 *   treemap source code
 *
 *    *    *    *    *    *    *    *    *    *
 *    Jan 2nd
 *     1. java 8 stream api / parallel stream / completable future
 *     2. sql / index...
 */
