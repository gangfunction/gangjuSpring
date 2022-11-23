package main.springbook.user.dao;

import javax.sql.DataSource;
import java.util.List;

public interface UserDao {
    void add(User user);
    User get(String id);
    void update(User user);

    List<User> getAll();
    void deleteAll();
    int getCount();

    void setDataSource(DataSource dataSource);
}
