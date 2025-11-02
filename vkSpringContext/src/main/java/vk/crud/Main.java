package vk.crud;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        UsersService service = context.getBean(UsersService.class);
        service.deleteAll();
        service.insert(new User(1L, "User1"));
        service.insert(new User(2L, "User2"));
        service.insert(new User(3L, "User3"));
        service.insert(new User(4L, "User4"));
        service.insert(new User(5L, "User5"));
        System.out.println(service.selectAll().stream().peek(System.out::println).filter(u -> true).count());
        service.update(new User(2L, "SupperUser2"));
        System.out.println(service.select(new User(2L, null)).username());
        service.delete(new User(4L, null));
        System.out.println(service.selectAll().stream().peek(System.out::println).filter(u -> true).count());
        service.deleteAll();
    }
}