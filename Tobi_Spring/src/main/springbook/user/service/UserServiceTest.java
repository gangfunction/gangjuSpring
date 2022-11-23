package main.springbook.user.service;

import main.springbook.learningtest.proxy.DynamicProxyTest;
import org.hamcrest.MatcherAssert;
import user.dao.User;
import user.dao.UserDao;
import user.domain.Level;
import mail.MailSender;
import org.aopalliance.aop.Advice;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "/applicationContext.xml")
public class UserServiceTest {
    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    UserService userService;
    @Autowired
    UserService testUserService;

    @Autowired
    UserDao userDao;

    @Autowired
    DataSource dataSource;

    @Autowired
    MailSender mailSender;

    @Autowired
    ApplicationContext context;


    @Test
    public void bean(){
        assertThat(userServiceImpl, is(notNullValue()));
    }

    List<User> users;

    @Before
    public void setUp(){
        users = Arrays.asList(
                new User("bumjin","박범진","p1", Level.BASIC,49,0),
                new User("kim","김진우","p2", Level.BASIC,50,0),
                new User("lee","이진우","p3", Level.VIP,49,0),
                new User("choi","최진우","p4", Level.VIP,60,30),
                new User("park","박진우","p5", Level.SUPER,100,100)
        );
    }
    @Test
    public void add(){
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userServiceImpl.add(userWithLevel);
        userServiceImpl.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));


    }

    private void checkLevelUpgraded(User user){
        User userUpdate = userDao.get(user.getId());
        if (false){
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        }
        else{
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() {

        userServiceImpl.upgradeLevels();

        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setMailSender((MockMailSender) mailSender);

        TxProxyFactoryBean txProxyFactoryBean=context.getBean("txProxyFactoryBean", TxProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserService);
        UserService txUserService = (UserService) txProxyFactoryBean.getObject();


    }


    @Test
    @DirtiesContext
    public void upgradeAllOrNothing() {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setMailSender(mailSender);

        TxProxyFactoryBean txProxyFactoryBean=context.getBean("txProxyFactoryBean", TxProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserService);
        UserService txUserService = (UserService) txProxyFactoryBean.getObject();

        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(testUserService);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern("upgradeLevels");


        userDao.deleteAll();
        for(User user: users) userDao.add(user);

        try{
            this.testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        }
        catch(Exception ignored){
        }
        checkLevelUpgraded(users.get(1));

    }
    @Test
    public void advisorAutoProxyCreator(){
        MatcherAssert.assertThat(testUserService,is(java.lang.reflect.Proxy.class));
    }
    @Test
    public void pointcutAdvisor(){
        ProxyFactoryBean pfBean= new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("add");
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, (Advice) new DynamicProxyTest.UppercaseAdvice()));

        DynamicProxyTest.Hello proxiedHello = (DynamicProxyTest.Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    @Test
    public void readOnlyTransactionAttribute(){
        testUserService.getAll();
    }
}
