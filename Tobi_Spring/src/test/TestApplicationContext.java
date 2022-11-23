package test;

import main.springbook.learningtest.proxy.DynamicProxyTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;

import main.springbook.user.dao.User;
import main.springbook.user.dao.UserDao;
import main.springbook.user.dao.UserDaoJdbc;
import main.springbook.user.domain.Level;
import main.springbook.user.service.DummyMailSender;
import main.springbook.user.service.TestUserService;
import main.springbook.user.sqlService.SqlService;
import main.springbook.user.java.mail.MailSender;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import main.springbook.user.service.UserService;
import main.springbook.user.service.UserServiceImpl;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "sqlService")
public class TestApplicationContext {


    @Autowired
    SqlService sqlService;
    @Bean
    public UserDao userDao(){
        UserDaoJdbc dao = new UserDaoJdbc();
        dao.setDataSource(dataSource());
        dao.setSqlService(sqlService);
        return dao;
    }
    @Bean
    public Mailsender mailsender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        return mailSender;
    }
    @Bean
    public UserService userService(){
        UserServiceImpl service = new UserServiceImpl();
        service.setUserDao(userDao());
        service.setMailSender(mailSender());
        return service;
    }
    @Bean
    public UserService testUserService(){
        TestUserService testService = new TestUserService();
        testService.setUserDao(userDao());
        testService.setMailSender(mailSender());
        return (UserService) testService;
    }
    @Bean
    public MailSender mailSender(){
        return new DummyMailSender();
    }








    @Autowired
    private UserDao dao;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private DataSource dataSource;
    private User user1;
    private User user2;
    private User user3;


    @Before
    public void setUp(){
        this.user1= new User("gyumee","박성철","p@ssw0rd", Level.BASIC,1,0);
        this.user2= new User("bumjin","박철","p@s3w0rd", Level.VIP,100,5);
        this.user3= new User("javajigi","성철","p4ssw0rd",Level.SUPER,100,5);
    }
    @Test
    public void classNamePointcutAdvisor(){
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut(){
            public ClassFilter getClassFilter(){
                return clazz -> clazz.getSimpleName().startsWith("HelloT")
            }
        };
        classMethodPointcut.setMappedName("sayH*");

        checkAdviced(new DynamicProxyTest.HelloTarget(), classMethodPointcut, true);
        class HelloWorld extends DynamicProxyTest.HelloTarget {}
        checkAdviced(new HelloWorld(), classMethodPointcut, false);
        class HelloToby extends DynamicProxyTest.HelloTarget {}
        checkAdviced(new HelloToby(), classMethodPointcut, true);
    }
    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced){
        ProxyFactoryBean pfBean=new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new DynamicProxyTest.UppercaseAdvice()));
        DynamicProxyTest.Hello proxiedHello = (DynamicProxyTest.Hello)pfBean.getObject();

        if (adviced){
            assertThat(Objects.requireNonNull(proxiedHello).sayHello("Toby"), is("HELLO TOBY"));
            assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
            assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
        }
        else{
            assertThat(Objects.requireNonNull(proxiedHello).sayHello("Toby"), is("Hello Toby"));
            assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
        }
    }


    @Test(expected= EmptyResultDataAccessException.class)
    public void getUserFailure() {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.get("unknown_id");
    }

    @Test
    public void addAndGet() {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userGet= dao.get(user1.getId());
        assertThat(userGet.getName(), is(user1.getName()));
        assertThat(userGet.getPassword(), is(user1.getPassword()));

        User userGet2= dao.get(user2.getId());
        assertThat(userGet2.getName(), is(user2.getName()));
        assertThat(userGet2.getPassword(), is(user2.getPassword()));


    }
    @Test
    public void count() {


        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        dao.add(user3);
        assertThat(dao.getCount(), is(3));


    }
    @Test
    public void getAll(){
        dao.deleteAll();

        dao.add(user1);
        List<User> users1= dao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2= dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3= dao.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));


    }
    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
        MatcherAssert.assertThat(user1.getLevel(), CoreMatchers.is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
    }
    @Test
    public void sqlExceptionTranslate(){
        dao.deleteAll();
        try{
            dao.add(user1);
            dao.add(user1);
        }
        catch (DuplicateKeyException e){
            SQLException sqlEx= (SQLException)e.getRootCause();
            SQLExceptionTranslator set =
                    new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            assertThat(set.translate(null,null, Objects.requireNonNull(sqlEx)), is(DataAccessException.class));
        }
    }
    @Test
    public void update(){
        dao.deleteAll();
        dao.add(user1);
        dao.add(user2);

        user1.setName("성철");
        user1.setPassword("p@ssw0rd");
        user1.setLevel(Level.VIP);
        user1.setLogin(100);
        user1.setRecommend(5);
        dao.update(user1);

        User user1Update= dao.get(user1.getId());
        checkSameUser(user1, user1Update);
        User user2Update= dao.get(user2.getId());
        checkSameUser(user2, user2Update);
    }
}

