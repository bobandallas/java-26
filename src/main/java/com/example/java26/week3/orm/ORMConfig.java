package com.example.java26.week3.orm;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


/**
 * why hibernate
 * Hibernate
 *      1. Hql
 *      2. object mapping
 *      3. cache
 *      4. connection pool
 *          unused connection blocking queue
 *          used connection blocking queue
 *      5. criteria  query
 *      ...
 *
 *      datasource 1 - 1 session factory 1 - m session 1 - 1 user
 *      save or update
 * JPA
 *      datasource 1 - 1 entity manager factory 1 - m entity manager 1 - 1 user
 *      persist
 *      merge
 *
 * Spring data jpa
 *      developer -> create interface .. (dynamic proxy implementation)
 *
 *    *     *     *     *     *     *     *     *     *     *     *     *     *
 * how to use hibernate
 *    1. configure pom.xml (dependency)
 *    2. configure datasource
 *    3. create entity class
 *    4. create repository interface
 *       impl interface
 *
 *    *     *     *     *     *     *     *     *     *     *     *     *     *
 *   Student s = (Student) em.createQuery(select s from Student s where id = )
 *   eager loading:
 *      s.id
 *      s.name
 *      s.getTeacher_Student()
 *   lazy loading:
 *      s.id
 *      s.name
 *      s.getTeacher_Student() => trigger another sql query
 *
 *    *     *     *     *     *     *     *     *     *     *     *     *     *
 *   List<Student> list = em.createQuery(select s from Student s)
 *   for(Student s: list) {
 *       s.getTeacher_Student().size();
 *   }
 *    *     *     *     *     *     *     *     *     *     *     *     *     *
 *   stage / status of instances
 *   persist - attach / proxy (persistent context) - detach / un-proxy
 *
 *    *     *     *     *     *     *     *     *     *     *     *     *     *
 *  Tomorrow:
 *      Spring IOC, AOP
 *      Spring Boot
 */

public class ORMConfig {

    private DataSource getDataSource() {
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
//        dataSource.setDatabaseName("OrmDemo");
        dataSource.setUser("postgres");
        dataSource.setPassword("password");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/university");
        return dataSource;
    }

    private Properties getProperties() {
        final Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        properties.put("hibernate.show_sql", "true");
        return properties;
    }

    private EntityManagerFactory entityManagerFactory(DataSource dataSource, Properties hibernateProperties) {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com/example/java26/week3/orm");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(hibernateProperties);
        em.setPersistenceUnitName("demo-unit");
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        em.afterPropertiesSet();
        return em.getObject();
    }

    public static void main(String[] args) {
        ORMConfig ormConfig = new ORMConfig();
        DataSource dataSource = ormConfig.getDataSource();
        Properties properties = ormConfig.getProperties();
        EntityManagerFactory entityManagerFactory = ormConfig.entityManagerFactory(dataSource, properties);

        EntityManager em = entityManagerFactory.createEntityManager();
        PersistenceUnitUtil unitUtil = entityManagerFactory.getPersistenceUnitUtil();

        getStudentById(em);
    }



    private static void insertToStudent(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Student s = new Student();
        s.setName("JerryTest");
        s.setId("50");
        em.merge(s);
        tx.commit();
    }
/*
select student0_.id as id1_0_, student0_.name as name2_0_ from student student0_ left outer join teacher_student teacher_st1_ on student0_.id=teacher_st1_.s_id where student0_.id=?

 */
    private static void getStudentById(EntityManager em) {
        Query query = em.createQuery("select s from Student s left join  s.teacher_students ts where s.id = ?1");
        query.setParameter(1, "17");
        Student s = (Student)query.getSingleResult();
        System.out.println(s.getTeacher_students());
    }

    private static void addToJunctionTable1(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Student s = new Student();
        s.setName("30th stu");
        Teacher t = new Teacher();
        //persist t first to get new id
        em.persist(t);
        t.setName("30th teacher");
        //build connection between t and s
        Teacher_Student ts = new Teacher_Student();
        ts.setStu(s);
        ts.setTeacher(t);
        t.addTeacher_students(ts);
        em.persist(s);
        tx.commit();
    }

    private static void addToJunctionTable2(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createNativeQuery("INSERT INTO TEACHER_STUDENT (S_ID, T_ID) VALUES (?, ?)");
        query.setParameter(1, 4);
        query.setParameter(2, 4);
        query.executeUpdate();
        tx.commit();
    }

    private static void addToJunctionTable3(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Student s = em.find(Student.class, "14");
        Teacher t = em.find(Teacher.class, "9");
        Teacher_Student ts = new Teacher_Student();
        ts.setStu(s);
        ts.setTeacher(t);
        em.persist(ts);
        tx.commit();
    }

    private static void addToJunctionTable4(EntityManager em) {
        em.getTransaction().begin();
        Student s = new Student();
        s.setId("14");
        Teacher t = new Teacher();
        t.setId("12");
        Teacher_Student ts = new Teacher_Student();
        ts.setStu(s);
        ts.setTeacher(t);
        em.persist(ts);
        em.getTransaction().commit();
    }

    private static void notOrphanRemoveBiRelation(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createQuery("select s from Student s join fetch s.teacher_students ts where s.id = ?1");
        query.setParameter(1, "5");
        Student s = (Student) query.getSingleResult();
        Teacher t = em.find(Teacher.class, "3");
        List<Teacher_Student> teacher_students = new ArrayList<>();
        for(Teacher_Student ts: s.getTeacher_students()) {
            if(ts.getTeacher().getId().equals(t.getId())) {
                teacher_students.add(ts);
                em.remove(ts);
            }
        }
        s.getTeacher_students().removeAll(teacher_students);
        t.getTeacher_students().removeAll(teacher_students);
        tx.commit();
    }

    private static void notOrphanRemoveSingleRelation(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createQuery("select s from Student s join fetch s.teacher_students ts where s.id = ?1");
        query.setParameter(1, "5");
        Student s = (Student) query.getSingleResult();
        for(Teacher_Student ts: s.getTeacher_students()) {
            em.remove(ts);
        }
        s.setTeacher_students(new ArrayList<>());
        tx.commit();
    }

    private static void orphanRemove(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createQuery("select s from Student s join s.teacher_students ts where s.id = ?1");
        query.setParameter(1, "14");
        Student s = (Student) query.getSingleResult();
        Iterator<Teacher_Student> itr = s.getTeacher_students().iterator();
        while(itr.hasNext()) {
            Teacher_Student ts = itr.next();
            if(ts.getTeacher().getId().equals("9")) {
                itr.remove();
            }
        }
        tx.commit();
    }


    private static void withoutOrphanRemove(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createQuery("select s from Student s join fetch s.teacher_students ts where s.id = ?1");
        query.setParameter(1, "14");
        Student s = (Student) query.getSingleResult();
        Iterator<Teacher_Student> itr = s.getTeacher_students().iterator();
        while(itr.hasNext()) {
            Teacher_Student ts = itr.next();
            if(ts.getTeacher().getId().equals("9")) {
                itr.remove();
                em.remove(ts);
            }
        }
        tx.commit();
    }
}
