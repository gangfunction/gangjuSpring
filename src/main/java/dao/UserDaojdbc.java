package dao;

import org.springframework.jdbc.core.JdbcTemplate;
import issuetracker.sqlService.SqlService;

import javax.sql.DataSource;
import java.util.List;

public class UserDaojdbc implements UserDao {


    private SqlService sqlService;
    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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
