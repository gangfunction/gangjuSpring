package main.springbook.user.dao;

import main.springbook.user.service.TestUserService;
import main.springbook.user.service.UserService;
import main.springbook.user.service.UserServiceImpl;
import main.springbook.user.sqlService.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
@Component
public class UserDaoJdbc implements UserDao {

    @Autowired
    UserDao userDao;
    private JdbcTemplate.BatchUpdateStatementCallback sqlService;

    @Bean
    public UserService userService(){
        UserServiceImpl service=new UserServiceImpl();
        service.setUserDao(this.userDao);
        service.setMailSender(mailSender());
        return service;
    }
    @Bean
    public UserService testUserService(){
        TestUserService testService = new TestUserService();
        testService.setUserDao(this.userDao);
        testService.setMailSender(mailSender());
        return (UserService) testService;
    }
    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Bean
    public UserDao userDao(){
        return new UserDaoJdbc();
    }


    public void add(User user) {
        this.jdbcTemplate.update(this.sqlService.getSql("userAdd"), user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(),user.getLogin(),user.getRecommend(), user.getEmail());

    public User get(String id) {
        return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGet"),new Object[]{id},this.userMapper);
    }

    public void deleteAll() {
        this.jdbcTemplate.update(this.sqlService.getSql("userDeleteAll"));
    }

    public int getCount(){
        return this.jdbcTemplate.queryForInt(this.sqlService.getSql("userGetCount"));
    }

    public void update(User user){
        this.jdbcTemplate.update(this.sqlService.getSql("userUpdate"),
                user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId());
    }


    public List<User> getAll() {
        return this.jdbcTemplate.query(this.sqlService.getSql("userGetAll"), this.userMapper);
    }

    }
