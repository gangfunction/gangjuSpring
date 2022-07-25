package service;

import dao.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
public interface UserService {
    void upgradeLevels();

    @Transactional(readOnly=true)
    List<User> getAll();


}
