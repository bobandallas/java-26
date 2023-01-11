package com.example.java26.week3;

import com.example.java26.week3.orm.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *  Spring
 *      IOC:
 *          IOC container
 *      Dependency Injection:
 *          1. IOC container: ApplicationContext(BeanFactory(map + factory pattern))
 *          2. class - level @Component / @Service / @Repository / @Controller
 *             method - level @Bean
 *             getApplicationContext().addBean()
 *          3. @Autowired + @Qualifier / @Primary
 *              field injection
 *              setter injection
 *              constructor injection
 *
 *              by type
 *              by name
 *          4. bean scope :
 *              singleton (default)
 *              prototype
 *              request
 *              session
 *              global session
 */

@SpringBootApplication
class SpringBasic {
    private static StudentService s1;
    private static StudentService s2;
    private static EntityManager em;

    @Autowired
    public SpringBasic(
            @Qualifier("aaa") StudentService xx1,
            @Qualifier("aaa") StudentService xx2,
            EntityManager em
    ) {
        s1 = xx1;
        s2 = xx2;
        SpringBasic.em = em;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBasic.class, args);
        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s1 == s2);
        System.out.println(em.find(Student.class, "15"));
    }
}
@Service
interface StudentService {}
@Service("aaa")
@Scope("prototype")
class StudentServiceImpl1 implements StudentService {
    @Override
    public String toString() {
        return "StudentServiceImpl1{}";
    }
}
@Service
//@Primary
class StudentServiceImpl2 implements StudentService {
    @Override
    public String toString() {
        return "StudentServiceImpl2{}";
    }
}

/**
 *  AOP(dynamic proxy)
 *
 * @AspectJ
 * class PublicMethodAspect {
 *      @Before
 *      @PointCut(public * *)
 *      public void xx() {
 *
 *      }
 *
 *      @Around
 *      public Object xx(JoinPoint mi) {
 *          //before
 *          Object obj = mi.proceed();
 *          //after
 *          return obj;
 *      }
 *
 *
 *      @After
 *      @PointCut(public * *)
 *      public void xx() {
 *
 *      }
 * }
 *
 *
 *      mi: List: [after1, before1, after2]
 *                                              i
 *      after1: {
 *          mi.proceed() {
 *              before1: {
 *                  execute before1 logic
 *                  return mi.proceed() {
 *                      after2: {
     *                      mi.proceed() {
     *                          execute target logic
     *                      }
     *
     *                      execute after2 logic
     *                      return res
 *                      }
 *                  }
 *              }
 *          }
 *
 *          execute after1 logic
 *          return result
 *      }
 *      before1, execute target logic, after2, after1
 *
 * Tomorrow :
 *      Network + server
 *
 * * * * * * * * * * * * *
 * question: create 2 threads, and print 1a2b3c4d..26z
 *
 * * * * * * * * * * * * *
 * filter -> dispatcher servlet -> handler mapping -> controller
 *
 * filter /endpoint -> servlet (method) -> service
 *
 */