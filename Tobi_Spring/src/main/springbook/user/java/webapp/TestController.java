package main.springbook.user.java.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    @RequestMapping("/test")
    public String home() {
        return "home";
    }
    @GetMapping("/test2")
    public void home2(String id, String pwd) {
        System.out.println(id);
        System.out.println(pwd);
    }
    @PostMapping("/test3")
    public String home3(Member m) {
        System.out.println(m);
        return "test3";
    }
}
