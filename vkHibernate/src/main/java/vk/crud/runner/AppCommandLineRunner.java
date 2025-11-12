package vk.crud.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vk.crud.model.User;
import vk.crud.service.UsersService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class AppCommandLineRunner implements CommandLineRunner {

    private UsersService usersService;

    private final Scanner scanner = new Scanner(System.in);

    public AppCommandLineRunner(@Autowired UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Spring Data JPA + Hibernate + Liquibase Demo ===");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> showAllUsers();
                case "2" -> addUser();
                case "3" -> findUserById();
                case "4" -> findUserByUsername();
                case "5" -> updateUser();
                case "6" -> deleteUser();
                case "7" -> deleteAllUsers();
                case "8" -> generateTestData();
                case "9" -> showUserStatistics();
                case "0" -> {
                    running = false;
                    System.out.println("Выход из приложения...");
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }

        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n=== МЕНЮ ===");
        System.out.println("1. Показать всех пользователей");
        System.out.println("2. Добавить пользователя");
        System.out.println("3. Найти пользователя по ID");
        System.out.println("4. Найти пользователя по имени");
        System.out.println("5. Обновить пользователя");
        System.out.println("6. Удалить пользователя");
        System.out.println("7. Удалить всех пользователей");
        System.out.println("8. Сгенерировать тестовые данные");
        System.out.println("9. Статистика пользователей");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void showAllUsers() throws SQLException {
        System.out.println("\n=== ВСЕ ПОЛЬЗОВАТЕЛИ ===");
        List<User> users = usersService.selectAll();

        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены.");
        } else {
            users.forEach(user -> System.out.printf(
                    "ID: %d | Username: %-15s | Email: %-20s | Created: %s%n",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getCreatedAt()
            ));
            System.out.println("Всего пользователей: " + users.size());
        }
    }

    private void addUser() {
        System.out.println("\n=== ДОБАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");
        System.out.print("Введите username: ");
        String username = scanner.nextLine();

        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        try {
            User user = new User(username, email);
            usersService.insert(user);
            System.out.println("✅ Пользователь успешно добавлен с ID: " + user.getId());
        } catch (Exception e) {
            System.out.println("❌ Ошибка при добавлении пользователя: " + e.getMessage());
        }
    }

    private void findUserById() {
        System.out.println("\n=== ПОИСК ПОЛЬЗОВАТЕЛЯ ПО ID ===");
        System.out.print("Введите ID пользователя: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());
            Optional<User> user = Optional.ofNullable(usersService.select(id));

            if (user.isPresent()) {
                User u = user.get();
                System.out.println("✅ Найден пользователь:");
                System.out.printf("ID: %d%n", u.getId());
                System.out.printf("Username: %s%n", u.getUsername());
                System.out.printf("Email: %s%n", u.getEmail());
                System.out.printf("Created: %s%n", u.getCreatedAt());
                System.out.printf("Updated: %s%n", u.getUpdatedAt() != null ? u.getUpdatedAt() : "Never");
            } else {
                System.out.println("❌ Пользователь с ID " + id + " не найден.");
            }
        } catch (NumberFormatException | SQLException e) {
            System.out.println("❌ Неверный формат ID.");
        }
    }

    private void findUserByUsername() throws SQLException {
        System.out.println("\n=== ПОИСК ПОЛЬЗОВАТЕЛЯ ПО ИМЕНИ ===");
        System.out.print("Введите username для поиска: ");
        String username = scanner.nextLine();

        List<User> users = usersService.selectAll();
        List<User> foundUsers = users.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(username.toLowerCase()))
                .toList();

        if (foundUsers.isEmpty()) {
            System.out.println("❌ Пользователи с именем '" + username + "' не найдены.");
        } else {
            System.out.println("✅ Найдено пользователей: " + foundUsers.size());
            foundUsers.forEach(user -> System.out.printf(
                    "ID: %d | Username: %s | Email: %s%n",
                    user.getId(), user.getUsername(), user.getEmail()
            ));
        }
    }

    private void updateUser() {
        System.out.println("\n=== ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");
        System.out.print("Введите ID пользователя для обновления: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());
            Optional<User> userOpt = Optional.ofNullable(usersService.select(id));

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("Текущие данные:");
                System.out.printf("Username: %s%n", user.getUsername());
                System.out.printf("Email: %s%n", user.getEmail());

                System.out.print("Новый username (оставьте пустым для сохранения текущего): ");
                String newUsername = scanner.nextLine();
                if (!newUsername.isBlank()) {
                    user.setUsername(newUsername);
                }

                System.out.print("Новый email (оставьте пустым для сохранения текущего): ");
                String newEmail = scanner.nextLine();
                if (!newEmail.isBlank()) {
                    user.setEmail(newEmail);
                }

                usersService.update(user);
                System.out.println("✅ Пользователь успешно обновлен.");
            } else {
                System.out.println("❌ Пользователь с ID " + id + " не найден.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Неверный формат ID.");
        } catch (Exception e) {
            System.out.println("❌ Ошибка при обновлении пользователя: " + e.getMessage());
        }
    }

    private void deleteUser() {
        System.out.println("\n=== УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");
        System.out.print("Введите ID пользователя для удаления: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());
            Optional<User> user = Optional.ofNullable(usersService.select(id));

            if (user.isPresent()) {
                System.out.printf("Вы уверены, что хотите удалить пользователя '%s' (ID: %d)? (y/n): ",
                        user.get().getUsername(), id);
                String confirmation = scanner.nextLine();

                if ("y".equalsIgnoreCase(confirmation)) {
                    usersService.delete(id);
                    System.out.println("✅ Пользователь успешно удален.");
                } else {
                    System.out.println("❌ Удаление отменено.");
                }
            } else {
                System.out.println("❌ Пользователь с ID " + id + " не найден.");
            }
        } catch (NumberFormatException | SQLException e) {
            System.out.println("❌ Неверный формат ID.");
        }
    }

    private void deleteAllUsers() throws SQLException {
        System.out.println("\n=== УДАЛЕНИЕ ВСЕХ ПОЛЬЗОВАТЕЛЕЙ ===");
        List<User> users = usersService.selectAll();

        if (users.isEmpty()) {
            System.out.println("❌ Нет пользователей для удаления.");
            return;
        }

        System.out.printf("Вы уверены, что хотите удалить ВСЕХ пользователей (%d записей)? (y/n): ", users.size());
        String confirmation = scanner.nextLine();

        if ("y".equalsIgnoreCase(confirmation)) {
            usersService.deleteAll();
            System.out.println("✅ Все пользователи успешно удалены.");
        } else {
            System.out.println("❌ Удаление отменено.");
        }
    }

    private void generateTestData() {
        System.out.println("\n=== ГЕНЕРАЦИЯ ТЕСТОВЫХ ДАННЫХ ===");

        String[] testUsers = {
                "ivan:ivan@example.com",
                "maria:maria@example.com",
                "alex:alex@example.com",
                "olga:olga@example.com",
                "dmitry:dmitry@example.com"
        };

        int added = 0;
        for (String userData : testUsers) {
            String[] parts = userData.split(":");
            if (parts.length == 2) {
                try {
                    User user = new User(parts[0], parts[1]);
                    usersService.insert(user);
                    added++;
                    System.out.printf("✅ Добавлен: %s (%s)%n", parts[0], parts[1]);
                } catch (Exception e) {
                    System.out.printf("❌ Ошибка при добавлении %s: %s%n", parts[0], e.getMessage());
                }
            }
        }
        System.out.println("Генерация завершена. Добавлено пользователей: " + added);
    }

    private void showUserStatistics() throws SQLException {
        System.out.println("\n=== СТАТИСТИКА ПОЛЬЗОВАТЕЛЕЙ ===");
        List<User> users = usersService.selectAll();

        System.out.println("Общее количество пользователей: " + users.size());

        if (!users.isEmpty()) {
            User oldestUser = users.stream()
                    .min(Comparator.comparing(User::getCreatedAt))
                    .orElse(null);
            User newestUser = users.stream()
                    .max(Comparator.comparing(User::getCreatedAt))
                    .orElse(null);

            long updatedCount = users.stream()
                    .filter(u -> u.getUpdatedAt() != null)
                    .count();

            System.out.println("Самый старый пользователь: " +
                    (oldestUser != null ? oldestUser.getUsername() + " (" + oldestUser.getCreatedAt() + ")" : "N/A"));
            System.out.println("Самый новый пользователь: " +
                    (newestUser != null ? newestUser.getUsername() + " (" + newestUser.getCreatedAt() + ")" : "N/A"));
            System.out.println("Обновлено пользователей: " + updatedCount + " из " + users.size());
        }
    }
}