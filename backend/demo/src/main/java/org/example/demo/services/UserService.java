package org.example.demo.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.demo.entities.User;
import org.example.demo.repositories.UserRepository;
import org.example.demo.requests.CreateUserRequest;
import org.example.demo.requests.UpdateUserRequest;
import org.example.demo.util.ServiceResponse;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    @Inject
    private UserRepository userRepository;

    private static final String JWT_SECRET = "raf_novosti_secret_2024";

    public Map<String, Object> login(String email, String password) {

        if (email == null || email.isBlank() || password == null || password.isBlank())
            return ServiceResponse.error("Email i lozinka su obavezni", 400);

        User user = userRepository.findByEmail(email);

        if (user == null)
            return ServiceResponse.error("Nevalidni kredencijali", 401);

        if ("INACTIVE".equals(user.getStatus()))
            return ServiceResponse.error("Vas nalog je deaktiviran. Kontaktirajte administratora.", 403);

        String hashedPassword = DigestUtils.sha256Hex(password);

        if (!hashedPassword.equals(user.getPassword()))
            return ServiceResponse.error("Nevalidni kredencijali", 401);

        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + 24L * 60 * 60 * 1000);

        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        String token = JWT.create()
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withSubject(String.valueOf(user.getId()))
                .withClaim("email", user.getEmail())
                .withClaim("type", user.getType())
                .withClaim("firstName", user.getFirstName())
                .withClaim("lastName", user.getLastName())
                .sign(algorithm);

        Map<String, Object> response = new HashMap<>();

        response.put("jwt", token);
        response.put("type", user.getType());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        return response;
    }

    public User getUserFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);

            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);

            Integer userId = Integer.parseInt(jwt.getSubject());

            return userRepository.findById(userId);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getAllUsers(int page, int pageSize, User currentUser) {

        if (!"ADMIN".equals(currentUser.getType()))
            return ServiceResponse.error("Nemate dozvolu", 403);

        List<User> users = userRepository.findAll(page, pageSize);

        users.forEach(u -> u.setPassword(null));

        int total = userRepository.countAll();

        Map<String, Object> result = new HashMap<>();

        result.put("users", users);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    public Map<String, Object> createUser(CreateUserRequest request, User currentUser) {

        if (!"ADMIN".equals(currentUser.getType()))
            return ServiceResponse.error("Nemate dozvolu", 403);

        if (request.getFirstName() == null || request.getFirstName().isBlank() ||
                request.getLastName() == null || request.getLastName().isBlank() ||
                request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank())

            return ServiceResponse.error("Sva polja su obavezna", 400);

        if (!request.getPassword().equals(request.getConfirmPassword()))
            return ServiceResponse.error("Lozinke se ne poklapaju", 400);

        if (userRepository.existsByEmail(request.getEmail()))
            return ServiceResponse.error("Korisnik sa ovim email-om vec postoji", 409);

        if (!"ADMIN".equals(request.getType()) && !"CONTENT_CREATOR".equals(request.getType()))
            return ServiceResponse.error("Tip korisnika mora biti ADMIN ili CONTENT_CREATOR", 400);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setType(request.getType());
        user.setStatus("ACTIVE");
        user.setPassword(DigestUtils.sha256Hex(request.getPassword()));

        User saved = userRepository.save(user);
        saved.setPassword(null);
        return ServiceResponse.success("user", saved);
    }

    public Map<String, Object> updateUser(Integer userId, UpdateUserRequest request, User currentUser) {

        if (!"ADMIN".equals(currentUser.getType()))
            return ServiceResponse.error("Nemate dozvolu", 403);

        if (request.getFirstName() == null || request.getFirstName().isBlank() ||
                request.getLastName() == null || request.getLastName().isBlank() ||
                request.getEmail() == null || request.getEmail().isBlank())
            return ServiceResponse.error("Sva polja su obavezna", 400);

        User existing = userRepository.findById(userId);
        if (existing == null)
            return ServiceResponse.error("Korisnik nije pronadjen", 404);

        if (!existing.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail()))
            return ServiceResponse.error("Korisnik sa ovim email-om vec postoji", 409);

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setEmail(request.getEmail());
        existing.setType(request.getType());

        User updated = userRepository.update(existing);
        updated.setPassword(null);
        return ServiceResponse.success("user", updated);
    }

    public Map<String, Object> toggleUserStatus(Integer userId, User currentUser) {

        if (!"ADMIN".equals(currentUser.getType()))
            return ServiceResponse.error("Nemate dozvolu", 403);

        User user = userRepository.findById(userId);
        if (user == null)
            return ServiceResponse.error("Korisnik nije pronadjen", 404);

        if ("ADMIN".equals(user.getType()))
            return ServiceResponse.error("Status administratora ne moze biti promenjen", 400);

        String newStatus = "ACTIVE".equals(user.getStatus()) ? "INACTIVE" : "ACTIVE";
        user.setStatus(newStatus);
        userRepository.update(user);

        return ServiceResponse.success("status", newStatus);
    }

    public User findById(Integer id) {
        User user = userRepository.findById(id);
        if (user != null) user.setPassword(null);
        return user;
    }
}