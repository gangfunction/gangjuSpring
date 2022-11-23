package main.springbook.user.service;

import com.sun.jdi.connect.Transport;
import user.dao.User;
import user.dao.UserDao;
import user.domain.Level;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
@Service("userService")
public class UserServiceImpl implements UserService {
    UserDao userDao;

    public void getAll(){
        userDao.getAll();
    }


    public UserServiceImpl() {
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    public static final int MIN_LOGCOUNT_FOR_VIP = 50;
    public static final int MIN_RECOMMEND_FOR_SUPER = 30;

    public boolean canUpgradeLevel (User user){
        Level currentLevel = user.getLevel();
        return switch (currentLevel) {
            case BASIC -> (user.getLogin() >= MIN_LOGCOUNT_FOR_VIP);
            case VIP -> (user.getRecommend() >= MIN_RECOMMEND_FOR_SUPER);
            case SUPER -> false;
        };
    }

    public void setUserDao(UserDao userdao) {
        this.userDao = userdao;
    }

    public void upgradeLevel(User user){
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEMail(user);
    }

    private void sendUpgradeEMail(User user) {
        Properties props = new Properties();
        props.put("mail.smtp.host","mail.ksug.org");
        Session session= Session.getInstance(props,null);
        MimeMessage message = new MimeMessage(session);
        try{
            message.setFrom(new InternetAddress("useradmin@ksug.org"));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(user.getEmail()));
            message.setSubject("[알림] 계정 레벨 업그레이드");
            Transport.send(message);
        }catch (AddressException | MessagingException | UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }


    }


    public void add(User user){
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }

}
