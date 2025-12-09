package vaultWeb.services;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vaultWeb.exceptions.DuplicateUsernameException;
import vaultWeb.models.User;
import vaultWeb.repositories.ChatImageRepo;
import vaultWeb.repositories.UserRepository;

/**
 * Service class for managing users.
 *
 * <p>Provides functionality for user registration, checking for existing usernames, and retrieving
 * all users. Passwords are securely encoded before storing.
 */
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Registers a new user by encoding their password and assigning the default role.
   *
   * <p>Steps performed by this method:
   *
   * <ol>
   *   <li>Check if the username already exists in the database. If so, throw {@link
   *       DuplicateUsernameException}.
   *   <li>Encode the plaintext password using the injected {@link PasswordEncoder}.
   *   <li>Save the user entity with the hashed password to the database via {@link UserRepository}.
   * </ol>
   *
   * <p>Important: - The PasswordEncoder bean must match the encoder used during authentication to
   * correctly verify passwords.
   *
   * @param user The {@link User} entity containing username and plaintext password.
   * @throws DuplicateUsernameException if a user with the same username already exists.
   */
  public void registerUser(User user) {
    if (usernameExists(user.getUsername())) {
      throw new DuplicateUsernameException(
          "Username '" + user.getUsername() + "' is already taken");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
  }

  /**
   * Checks if a username already exists in the database.
   *
   * @param username The username to check.
   * @return {@code true} if the username exists, {@code false} otherwise.
   */
  public boolean usernameExists(String username) {
    return userRepository.existsByUsername(username);
  }

  /**
   * Retrieves a list of all registered users.
   *
   * @return A {@link List} of {@link User} entities.
   */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }
  private final ChatImageRepo chatImageRepo;

  public String uploadChatImage(byte[] imageBytes, Integer senderUserId, Integer receiverUserId){
    // Use native insert with explicit bytea cast to avoid driver/type mismatch issues

    OffsetDateTime createdon = OffsetDateTime.now();
    chatImageRepo.saveImage(imageBytes, senderUserId, receiverUserId, createdon);

    return "Image uploaded successfully";
  }
}
