package main.springbook.user.service;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
    void upgradeLevels();

    @Transactional(readOnly=true)
    void getAll();


}
