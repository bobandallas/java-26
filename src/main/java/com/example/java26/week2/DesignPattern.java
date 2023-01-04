package com.example.java26.week2;

import java.io.Serializable;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  tomorrow: Design pattern + reflection
 *  Singleton
 */
//eager loading
class EagerLoadingSingleton {
    private static final EagerLoadingSingleton instance = new EagerLoadingSingleton();

    private EagerLoadingSingleton() {}

    public static EagerLoadingSingleton getInstance() {
        return instance;
    }
}

class LazyLoadingSingleton {
    private static volatile LazyLoadingSingleton instance;
    private final Map<Integer, Integer> map;

    private LazyLoadingSingleton() {
        map = new ConcurrentHashMap<>();
    }

    public static LazyLoadingSingleton getInstance() {
        if(instance == null) {
            synchronized (LazyLoadingSingleton.class) {
                if(instance == null) {
                    instance = new LazyLoadingSingleton();
                }
            }
        }
        return instance;
    }

    public void put(int key, int val) {
        map.put(key, val);
    }
}

enum EnumSingleton {
    INSTANCE1;
}

class TestSingleton {
    public static void main(String[] args) throws Exception {
        Class clazz = LazyLoadingSingleton.class;
        Constructor constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        LazyLoadingSingleton instance = (LazyLoadingSingleton) constructor.newInstance();
        System.out.println(instance);

        instance.put(5, 5);
    }
}

/**
 *      1000 x new Car(...)
 *
 *      Factory
 *          1. loose coupling
 *          2. hide implementation
 */
class Car {
    private String name;
    private String type;
    private int year;
    private String id;


    public Car(String name, String type, int year, String id) {
        this.name = name;
        this.type = type;
        this.year = year;
        this.id = id;
    }
    //getter setter


    public static Car getCar(String name, String type) {
        return new Car(name, type, new Date().getYear(), null);
    }
}
class CarFactory {
    public static Car getCar(String name, String type) {
        return new Car(name, type, new Date().getYear(), null);
    }
}

/**
 *      builder
 */
class Student {
    @MyAnnotation(name = "aaaaaabbbcc")
    private String name;
    private int age;

    public Student() {
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public Student setName(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public Student setAge(int age) {
        this.age = age;
        return this;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

class StudentBuilder {
    private String name;
    private int age;

    public StudentBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public StudentBuilder setAge(int age) {
        this.age = age;
        return this;
    }

    public Student build() {
        return new Student(name, age);
    }
}

class BuilderTest {
    public static void main(String[] args) {
        Student s1 = new Student().setAge(5).setName("aaa");
        Student s2 = new StudentBuilder().setAge(5).setName("aaa").build();
    }
}


/**
 *      composition(Has-A) vs aggregation(Is-A)
 *
 *      class LinkedList {
 *          public static xx func1() {
 *              new Node()
 *          }
 *          private class Node {
 *              Node prev;
 *              Node next;
 *          }
 *      }
 *
 *      class TreeNode {
 *          TreeNode left;
 *          TreeNode right;
 *      }
 */

/**
 *      template
 *          abstract class Car {
 *              private String type;
 *              private int year;
 *
 *              public void start() {
 *                  //..
 *              }
 *
 *              abstract void stop();
 *          }
 *
 *          class BMWCar extends Car {}
 *          class HondaCar extends Car {}
 */

/**
 *      prototype == clone
 */

/**
 *      bridge
 *
 *      class A {
 *          private B b;
 *
 *          public A(B b) {
 *              this.b = b;
 *          }
 *
 *          public void func1() {
 *              int x = b.get();
 *          }
 *      }
 *
 *      interface B {}
 *      class BImpl1 implements B {}
 *      class BImpl2 implements B {}
 */


/**
 *      strategy
 *
 *
 */
interface Calculate {
    int execute(int v1, int v2);
}
class Calculator {
//    private Calculate calculate;
//
//    public Calculator(Calculate calculate) {
//        this.calculate = calculate;
//    }

    public int run(int v1, int v2, Calculate calculate) {
        return calculate.execute(v1, v2);
    }
}

class CalculatorTest {
    public static void main(String[] args) {
        int res = new Calculator().run(0, 1, (s1, s2) -> s1 * s2);
        System.out.println(res);
    }
}

/**
 *       facade == api gateway
 */

/**
 *      observer == publisher + subscriber
 */
class Topic {
    private final List<Observer> observers = new ArrayList<>();

    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    public void publish(String msg) {
        for(Observer observer: observers) {
            observer.receive(msg);
        }
    }
}

class Observer {
    public void receive(String msg) {
        System.out.println(msg);
    }
}

/**
 *      adaptor
 */
//3rd party library
class A {}
class B {}
interface Mapper {
    B mapping (A a);
}
class MapperImpl implements Mapper {
    @Override
    public B mapping(A a) {
        return null;
    }
}
//-------------------------------
interface MyMapper {
    B mapping (A a);
}
class ProductService {
    private MyMapper mapper;

    public ProductService(MyMapper mapper) {
        this.mapper = mapper;
    }

    public void func1(A a) {
        B b = mapper.mapping(a);
    }

    public static void main(String[] args) {
        new ProductService(new MapperAdaptor(new MapperImpl()));
    }
}

class MapperAdaptor implements MyMapper {
    private Mapper mapper;

    public MapperAdaptor(Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public B mapping(A a) {
        return mapper.mapping(a);
    }
}

/**
 *      static proxy / decorator
 */

//class level proxy (inheritance)

class ProxyClassMapper extends MapperImpl {
    @Override
    public B mapping(A a) {
        System.out.println("before");
        B b = super.mapping(a);
        System.out.println("after");
        return b;
    }
}

//interface implementation

class ProxyInterfaceMapper implements Mapper {
    private Mapper mapper;

    public ProxyInterfaceMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public B mapping(A a) {
        System.out.println("before");
        B b = mapper.mapping(a);
        System.out.println("after");
        return b;
    }
}
/**
 *      dynamic proxy
 */

//class level dynamic proxy : cglib
//interface level dynamic proxy: java proxy

class MyInvocationHandler implements InvocationHandler {
    private Calculate obj;

    public MyInvocationHandler(Calculate obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before");
        System.out.println(method);
        //method.invoke(obj, args) == obj.method(args)
        Object res = method.invoke(obj, args);
        System.out.println(res);
        System.out.println("after");
        return res;
    }
}

class ProxyTest {
    public static void main(String[] args) {
        Calculate proxy = (Calculate) Proxy.newProxyInstance(
                ProxyTest.class.getClassLoader(),
                new Class[]{Calculate.class},
                new MyInvocationHandler((v1, v2) -> v1 + v2)
        );
        int res = proxy.execute(1, 2);
//        System.out.println(res);
    }
}

/**
 *  reflection
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface MyAnnotation {
    String name() default "Tom";
}

class ReflectionDemo {
    public static void main(String[] args) throws Exception {
        Class clazz = Student.class;
        Student s1 = (Student) clazz.getDeclaredConstructors()[0].newInstance();
        Field f = clazz.getDeclaredField("name");

        System.out.println(s1);
        f.setAccessible(true);

        Annotation annotation = f.getDeclaredAnnotation(MyAnnotation.class);
        MyAnnotation m = (MyAnnotation) annotation;
        f.set(s1, m.name());
        System.out.println(s1);
    }
}

/**
 *  oracle live sql
 *  oracle sql
 */