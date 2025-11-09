package vk.crud;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import vk.crud.config.AppConfig;
import vk.crud.model.User;
import vk.crud.service.UsersService;

import java.sql.SQLException;
import java.util.List;

@ComponentScan()
public class Main {

    public static void main(String[] args) throws SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
        UsersService service = context.getBean(UsersService.class);
        service.deleteAll();
        service.insert(new User(1L, "User1"));
        service.insert(new User(2L, "User2"));
        service.insert(new User(3L, "User3"));
        service.insert(new User(4L, "User4"));
        service.insert(new User(5L, "User5"));
        List<User> users = service.selectAll();
        users.forEach(System.out::println);
        System.out.println(users.size());
        service.update(new User(2L, "SupperUser2"));
        System.out.println(service.select(new User(2L, null)).username());
        service.delete(new User(4L, null));
        users = service.selectAll();
        users.forEach(System.out::println);
        System.out.println(users.size());
        service.deleteAll();
    }

}