package service;

import dao.User;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

public class TestUserService extends UserServiceImpl {
    static final PlatformTransactionManager transactionManager = null;
    private final String id;
    public TestUserService(String id){
        super();
        this.id = id;
    }
    public List<User> getAll(){
        for(User user: super.getAll()){
            super.update(user);
            }
        return null;
    }
    public void upgradeLevel(User user){
        if(user.getId().equals(this.id)) throw new TestUserServiceException();
        super.upgradeLevel(user);
    }

}
