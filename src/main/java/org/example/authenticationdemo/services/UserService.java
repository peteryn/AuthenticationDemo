package org.example.authenticationdemo.services;

import org.example.authenticationdemo.models.User;
import org.example.authenticationdemo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.sql.SQLOutput;
import java.util.Base64;

import static java.util.Base64.getDecoder;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean registerUser(User user) {
        return userRepository.insert(user);
    }

    public boolean loginUser(String email, String password) {
        Base64.Decoder decoder = Base64.getDecoder();
        Base64.Encoder encoder = Base64.getEncoder();

        // retrieve user from database based on email
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            return false;
        }

        // use salt from database retrieved object
        String encodedSalt = user.getSalt();
        byte[] decodedSalt = decoder.decode(encodedSalt);

        // apply salt to parameter password, then hash
        byte[] saltedPassword = addSaltToPassword(password, decodedSalt);
        byte[] hashedAndSaltedPassword = hashSaltedPassword(saltedPassword);

        // encode hashedAndSaltedPassword as String
        String encodedHashedAndSaltedPassword = encoder.encodeToString(hashedAndSaltedPassword);

        // check if result equals the password from db
        return encodedHashedAndSaltedPassword.equals(user.getPassword());
    }

    private byte[] addSaltToPassword(String password, byte[] saltBytes) {
        byte[] passwordBytes = password.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(passwordBytes.length + saltBytes.length);
        buffer.put(passwordBytes);
        buffer.put(saltBytes);

        return buffer.array();
    }

    private byte[] hashSaltedPassword(byte[] saltedPassword) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println("never should occur");
        }
        return md.digest(saltedPassword);
    }
}
