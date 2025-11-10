package vk.crud.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vk.crud.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    private final DataSource dataSource;

    public UserDaoImpl(@Autowired DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> selectAll() throws SQLException {
        List<User> result = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement("select id, username from users;");
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            result.add(new User(rs.getLong(1), rs.getString(2)));
        return result;
    }

    @Override
    public User select(User user) throws SQLException {
        User result = null;
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement("select id, username from users where id = ?");
        ps.setLong(1, user.id());
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            result = new User(rs.getLong(1), rs.getString(2));
        return result;
    }

    @Override
    public User insert(User user) throws SQLException {
        User result = null;
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement("insert into users (id, username) values (?, ?)");
        ps.setLong(1, user.id());
        ps.setString(2, user.username());
        if (ps.execute())
            result = user;
        return result;
    }

    @Override
    public User update(User user) throws SQLException {
        User result = null;
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement("update users set username = ? where id = ?");
        ps.setString(1, user.username());
        ps.setLong(2, user.id());
        if (ps.execute())
            result = user;
        return result;
    }

    @Override
    public User delete(User user) throws SQLException {
        User result = null;
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement("delete from users where id = ?");
        ps.setLong(1, user.id());
        if (ps.execute())
            result = user;
        return result;
    }

    @Override
    public void deleteAll() throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement("delete from users");
        ps.execute();
    }

}
