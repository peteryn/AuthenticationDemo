package org.example.authenticationdemo.services;

import org.example.authenticationdemo.models.User;
import org.example.authenticationdemo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SecureRandom secureRandom;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.secureRandom = new SecureRandom();
    }

    public boolean registerUser(String email, String password) {
        // get a new salt
        byte[] salt = getNewSalt();

        // apply salt to user password
        byte[] saltedPassword = addSaltToPassword(password, salt);

        // hash the salted password
        byte[] hashedSaltedPassword = hashSaltedPassword(saltedPassword);

        // encode the salt and the hashed salted password
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedPassword = encoder.encodeToString(hashedSaltedPassword);
        String encodedSalt = encoder.encodeToString(salt);

        // create user object
        User user = new User(email, encodedPassword, encodedSalt);

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
        } catch (NoSuchAlgorithmException _) {
            // should never happen because "MD5" is a known hashing algorithm
        }
        return md.digest(saltedPassword);
    }

    private byte[] getNewSalt() {
        byte[] salt = new byte[64];
        secureRandom.nextBytes(salt);
        return salt;
    }
}
