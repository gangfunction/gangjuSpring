import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.cglib.proxy.MethodInterceptor;
import proxy.UppercaseHandler;

import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DynamicProxyTest {
    @Test
    public void classNamePointcutAdvisor(){
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut(){
            public ClassFilter getClassFilter(){
                return clazz -> clazz.getSimpleName().startsWith("Hello");
            }
        };
        classMethodPointcut.setMappedName("sayH*");
        checkAdviced(new HelloTarget(), classMethodPointcut, true);
        class HelloWorld extends HelloTarget{};
        checkAdviced(new HelloWorld(), classMethodPointcut, false);
        class HelloToby extends HelloTarget{};
        checkAdviced(new HelloToby(), classMethodPointcut, false);
    }
    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello)pfBean.getObject();
        if(adviced){
            assertThat(proxiedHello.sayHello("Spring"), is("HELLO, SPRING"));
            assertThat(proxiedHello.sayHi("Spring"), is("HI, SPRING"));
            assertThat(proxiedHello.sayThankYou("Spring"), is("THANK YOU, SPRING"));
        }else{
            assertThat(proxiedHello.sayHello("Spring"), is("Hello, Spring"));
            assertThat(proxiedHello.sayHi("Spring"), is("Hi, Spring"));
            assertThat(proxiedHello.sayThankYou("Spring"), is("Thank you, Spring"));
        }
    }
    @Test
    public void simpleProxy(){
        Hello proxyHello=(Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class},
                new UppercaseHandler(new HelloTarget()));
        )
    }
    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }
    public static class UppercaseAdvice implements MethodInterceptor {
    }
    static interface Hello{
        String sayHello(String name);
        String sayHi(String name);
        String sayThankYou(String name);
    }
    static class HelloTarget implements Hello{
        public String sayHello(String name){
                return "Hello "+name;
            }
            public String sayHi(String name){
                return "Hi "+name;
            }
            public String sayThankYou(String name){
                return "Thank you "+name;
            }
        }

}
