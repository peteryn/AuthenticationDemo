package org.example.authenticationdemo.repositories;

import org.example.authenticationdemo.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean insert(User user) {
        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
        boolean result = false;
        try {
            jdbcTemplate.update(sql, user.getEmail(), user.getPassword());
            result = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean checkUserCredentials(User user) {
        String sql = "SELECT * FROM users WHERE email = '" + user.getEmail() + "' AND password = '"  + user.getPassword() + "'";
        User returned = jdbcTemplate.queryForObject(sql, (rs, row) -> new User(rs.getString("email"), rs.getString("password")));
        return returned != null;
    }
}
