package vk.crud;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    List<User> selectAll() throws SQLException;
    User select(User user) throws SQLException;
    User insert(User user) throws SQLException;
    User update(User user) throws SQLException;
    User delete(User user) throws SQLException;
    void deleteAll() throws SQLException;
}
