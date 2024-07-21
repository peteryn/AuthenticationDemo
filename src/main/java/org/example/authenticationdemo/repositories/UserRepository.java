package org.example.authenticationdemo.repositories;

import org.example.authenticationdemo.models.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean insert(User user) {
        String sql = "INSERT INTO users (email, password, salt) VALUES (?, ?, ?)";
        boolean result = false;
        try {
            jdbcTemplate.update(sql, user.getEmail(), user.getPassword(), user.getSalt());
            result = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean checkUserCredentials(User user) {
        String sql = "SELECT * FROM users WHERE email = '" + user.getEmail() + "' AND password = '"  + user.getPassword() + "'";
        var returned = jdbcTemplate.queryForList(sql);
        return !returned.isEmpty();
    }

    public void findUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = '" + email + "'";
        try {
            jdbcTemplate.queryForList(sql);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Nothing found");
            System.out.println(e.getMessage());
        }
    }
}
