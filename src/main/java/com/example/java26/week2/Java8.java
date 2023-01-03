package com.example.java26.week2;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *  java 8 new features
 *      1. perm gen -> meta space
 *      2. hashmap -> red black tree
 *      3. concurrent hashmap ->
 *
 *      4. functional interface
 *      5. lambda expression
 *      6. stream api
 *          res = func1(x -> x * 2).func2(x -> x - 3).func3(x -> x / 2).func4(get collections);
 *          functional programming
 *
 *        a. stream() / Arrays.stream()
 *        b. intermediate operation
 *           map(Function interface) : one input, one output
 *           sorted(Comparator)
 *           filter(Predicate)
 *           ..
 *
 *           terminal operation
 *           reduce()
 *           collect(..)
 *           forEach(Consumer interface) : one input, no output
 *
 *        c. Function : one input, one output
 *           Consumer : one input, no output
 *           Supplier : no input, one output
 *           Predicate: one input, boolean output
 *
 *
 *        list.stream().map().map().map()
 *        ReferencePipeline1(() -> iterator) <-> ReferencePipeline2 <-> ReferencePipeline3..
 *        list.stream().map().map().map().collect()
 *        Sink(iterator) -> Sink(map) -> Sink(map) -> Sink(map) -> Sink(sorted)
 */
class StreamTest1 {
    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 5, 4, 3, 2, 124, 124, 35, 325, 23234);
//        Supplier<Iterator> s = new Supplier<Iterator>() {
//            @Override
//            public Iterator get() {
//                return list.iterator();
//            }
//        };
//
//        s.get();


        //cpu core
        list.parallelStream() //() -> list.iterator()
                                .map(x -> x * 2) // 1 -> 2
                                .map(x -> x - 3) // 2 -> -1
                                .map(x -> x / 2) //-1 -> 0
                                .filter(x -> x > 0)
                                .sorted((x1, x2) -> x2 - x1)
                                .map(x -> x + 3);
//                                .forEach(x -> System.out.println(x)); //new list() -> add
//        System.out.println(res);

        Map<Integer, Long> cnt1 = list.stream()
                                        .collect(Collectors.groupingBy(x -> x, Collectors.counting()));
        Map<Integer, Integer> cnt2 = list.stream()
                                        .collect(
                                                () -> new HashMap<>(),
                                                (map, ele) -> map.put(ele, map.getOrDefault(ele, 0) + 1),
                                                (m1, m2) -> m1.putAll(m2)
                                        );
//        System.out.println(cnt1);
//        System.out.println(cnt2);

//        System.out.println(list.stream().reduce(0, (x1, x2) -> x1 + x2));


        //convert integer to string and collect / return string starts with '2'
        List<String> res = list.stream()
                .map(x -> String.valueOf(x))
                .distinct()
                .filter(x -> x.startsWith("2"))
                .collect(Collectors.toList());
        System.out.println(res);
    }
}


/**
 *      7. default method
 *      8. optional
 */
class OptionalTest {
    public static void main(String[] args) {
        Optional res = Optional.ofNullable(null);
        System.out.println(res.orElse(5));
    }
}
/**
 *      9. completable future
 *
 *      5 tasks
 *      t1 10s -> step2
 *      t2 5s  -> step2
 *      t3 3s  -> step3     -> allof    -> join()
 *      t4 7s  -> step5
 *      t5 6s  -> step3
 */
class CFTest1 {
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    public static void main(String[] args) {
        List<CompletableFuture<Integer>> list = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            final int val = i;
            CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return val;
            }, pool).thenApply(x -> x * 2).orTimeout(1000, TimeUnit.SECONDS);
            list.add(cf);
        }
        //solution1
//        int sum = 0;
//        for(CompletableFuture<Integer> cf: list) {
//            sum += cf.join();
//        }
//        System.out.println(sum);

        //solution2
        CompletableFuture<Integer> res = CompletableFuture.allOf(list.toArray(new CompletableFuture[0]))
                .thenApply(Void -> list.stream().map(cf -> cf.join()).reduce(0, (v1, v2) -> v1 + v2));
//        res.isDone()
        System.out.println(res.join());
//        System.out.println("aaa");

    }

    private static void func1() throws Exception {
        List<Future<Integer>> list = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            final int val = i;
            Future<Integer> future = pool.submit(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return val;
            });
            list.add(future);
        }
        int sum = 0;
        for(Future<Integer> f: list) {
            sum += f.get() * 2;
        }
    }
}




@FunctionalInterface
interface MyRunnable {
    void run();
}

class MyTest {
    public static void main(String[] args) {
        Comparator<Integer> cpt1 = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return 0;
            }
        };
        Comparator<Integer> cpt2 = (v1, v2) -> v2 - v1;

    }
}
/**
 *      10. method reference
 */

/**
 *  tomorrow: Design pattern + reflection
 *      Singleton
 *      Factory
 *      builder
 *      composition
 *      template
 *      prototype
 *      bridge
 *      strategy
 *      facade
 *      observer
 *      adaptor
 *      proxy / decorator
 *      dynamic proxy
 *
 */