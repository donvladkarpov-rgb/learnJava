package vk.crud.service;

import vk.crud.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UsersService {
    List<User> selectAll() throws SQLException;
    User select(Long id) throws SQLException;
    User select(String username) throws SQLException;
    User insert(User user) throws SQLException;
    User update(User user) throws SQLException;
    void delete(Long id) throws SQLException;
    void deleteAll() throws SQLException;
}
