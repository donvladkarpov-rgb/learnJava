package vk.crud;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
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
        service.insert(new User("User1", "User1@vtb.ru"));
        service.insert(new User("User2", "User2@vtb.ru"));
        service.insert(new User("User3", "User3@vtb.ru"));
        service.insert(new User("User4", "User4@vtb.ru"));
        service.insert(new User("User5", "User5@vtb.ru"));
        List<User> users = service.selectAll();
        users.forEach(System.out::println);
        System.out.println(users.size());
        User user2 = service.select("User2");
        user2.setUsername("SuperUser2");
        user2.setEmail("SuperUser2@vtb.ru");
        service.update(user2);
        System.out.println(service.select("SuperUser2").getId());
        service.delete(user2.getId());
        users = service.selectAll();
        users.forEach(System.out::println);
        System.out.println(users.size());
        service.deleteAll();
    }

}