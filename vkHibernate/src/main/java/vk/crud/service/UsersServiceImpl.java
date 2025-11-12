package vk.crud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vk.crud.dao.UserDao;
import vk.crud.model.User;

import java.sql.SQLException;
import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {

    private final UserDao userDao;

    public UsersServiceImpl(@Autowired UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> selectAll() throws SQLException {
        return userDao.findAll();
    }

    @Override
    public User select(Long id) throws SQLException {
        return userDao.getReferenceById(id);
    }

    @Override
    public User select(String username) throws SQLException {
        return userDao.findByUsername(username);
    }

    @Override
    public User insert(User user) throws SQLException {
        return userDao.save(user);
    }

    @Override
    public User update(User user) throws SQLException {
        return userDao.save(user);
    }

    @Override
    public void delete(Long id) throws SQLException {
        userDao.delete(userDao.getReferenceById(id));
    }

    @Override
    public void deleteAll() throws SQLException {
        userDao.deleteAll();
    }

}
