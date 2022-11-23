package main.springbook.user.service;

import mail.MailSender;
import main.springbook.user.dao.UserDao;
import main.springbook.user.java.mail.MailSender;
import user.dao.UserDao;

public class TestUserService {
    private MailSender mailSender;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }
    private UserDao userDao;

}
