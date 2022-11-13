package springbook.user.java.beans.factory;

import user.dao.UserDao;
import user.dao.UserDaoJdbc;
import springbook.user.sqlService.SqlRegistry;
import springbook.user.sqlService.SqlService;
import springbook.user.sqlService.updatable.EmbeddedDbSqlRegistry;
import mail.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class userdaoforUse {

    @Autowired
    SqlService sqlService;
    @Bean
    public UserDao userDao(){
        UserDaoJdbc dao=new UserDaoJdbc();
        dao.setDataSource(dataSource());
        dao.setSqlService(sqlService());
        return dao;
    }
    @Bean
    public UserService userService(){
        UserServiceImpl service=new UserServiceImpl();
        service.setUserDao(userDao());
        service.setMailSender(mailSender());
        return service;
    }
    @Bean
    public UserService testUserService(){
        TestUserService testService= new TestUserService();
        testService.setUserDao(userDao());
        testService.setMailSender(mailSender());
        return testService;
    }
    @Bean
    public MailSender mailSender(){
        return new DummyMailSender();
    }

    @Bean
    public DataSource embeddedDataSource(){
        return new EmbeddedDatabaseBuilder()
                .setName("embeddedDatabase")
                .setType(HSQL)
                .addScript("sqlRegistrySchema.sql")
                .build();
    }
    @Bean
    public SqlRegistry sqlRegistry(){
        EmbeddedDbSqlRegistry sqlRegistry =new EmbeddedDbSqlRegistry();
        sqlRegistry.setDataSource(embeddedDataSource());
        return sqlRegistry;
    }
}
