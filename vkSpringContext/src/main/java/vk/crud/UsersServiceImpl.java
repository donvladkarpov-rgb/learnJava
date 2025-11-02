package vk.crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return userDao.selectAll();
    }

    @Override
    public User select(User user) throws SQLException {
        return userDao.select(user);
    }

    @Override
    public User insert(User user) throws SQLException {
        return userDao.insert(user);
    }

    @Override
    public User update(User user) throws SQLException {
        return userDao.update(user);
    }

    @Override
    public User delete(User user) throws SQLException {
        return userDao.delete(user);
    }

    @Override
    public void deleteAll() throws SQLException {
        userDao.deleteAll();
    }

}
