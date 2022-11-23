package test;

import org.junit.Test;
import proxy.Hello;
import proxy.HelloTarget;
import proxy.HelloUppercase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class HelloTest {
    @Test
    public void simpleProxy() {
        Hello hello = new HelloUppercase(new HelloTarget());
        assertThat(hello.sayHello("Spring"), is("Hello, Spring"));
        assertThat(hello.sayHi("Spring"), is("Hi, Spring"));
        assertThat(hello.sayThankYou("Spring"), is("Thank you, Spring"));
    }
}