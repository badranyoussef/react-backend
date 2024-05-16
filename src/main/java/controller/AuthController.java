package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.AuthDAO;
import dao.UserDAO;
import dtos.TokenDTO;
import dtos.UserDTO;
import exceptions.APIException;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import persistence.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class AuthController {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String timestamp = dateFormat.format(new Date());

    private static ObjectMapper om = new ObjectMapper();
    public static Handler login(AuthDAO authDAO) {
        return ctx -> {
            ObjectNode node = om.createObjectNode();
            try {
                UserDTO user = ctx.bodyAsClass(UserDTO.class);

                User verifiedUser = authDAO.verifyUser(user.getEmail(), user.getPassword());
                String token = TokenController.createToken(new UserDTO(verifiedUser));
                ctx.status(HttpStatus.CREATED).json(new TokenDTO(token, verifiedUser));

            }catch (APIException e){
                throw new APIException(e.getStatusCode(), "Wrong password", e.getTimeStamp());
            }
        };
    }
    public static Handler logout(AuthDAO authDAO) {
        return ctx -> {
            ObjectNode node = om.createObjectNode();
            String header = ctx.header("Authorization");
            String token = header.split(" ")[1];
            TokenController.invalidateToken(token);
            ctx.attribute("user", null);
            ctx.json(node.put("msg","you are now logged out"));
        };
    }
    public static Handler register(UserDAO userDAO) {
        return ctx -> {
            ObjectNode node = om.createObjectNode();
            try {
                User user = ctx.bodyAsClass(User.class);
                User createdUser = userDAO.create(user);
                String token = TokenController.createToken(new UserDTO(createdUser));
                ctx.status(HttpStatus.CREATED).json(new TokenDTO(token, user.getEmail(), user.getRolesAsStrings()));
            } catch(EntityExistsException e){
                ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
                ctx.json(node.put("msg","User already exist"));
            }
        };
    }
    public static Handler resetPassword(AuthDAO authDAO) {
        return ctx -> {
            ObjectNode node = om.createObjectNode();
            try {
                User user = ctx.bodyAsClass(User.class);
                if(authDAO.doesUserExist(user.getEmail())){
                    //use another method to generate  temporary token
                    String token = TokenController.createToken(new UserDTO(user));
                    
                }
            } catch (EntityNotFoundException e){
                ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
                ctx.json(node.put("msg", e.getMessage()));
            }
        };
    }

    public static Handler authenticate() {
        // To check the users roles against the allowed roles for the endpoint (managed by javalins accessManager)
        // Checked in 'before filter' -> Check for Authorization header to find token.
        // Find user inside the token, forward the ctx object with userDTO on attribute
        // When ctx hits the endpoint it will have the user on the attribute to check for roles (ApplicationConfig -> accessManager)
        ObjectNode returnObject = om.createObjectNode();
        return (ctx) -> {
            if(ctx.method().toString().equals("OPTIONS")) {
                ctx.status(200);
                return;
            }
            String header = ctx.header("Authorization");
            if (header == null) {
                ctx.status(HttpStatus.FORBIDDEN).json(returnObject.put("msg", "Authorization header missing"));
                return;
            }
            String token = header.split(" ")[1];
            if (token == null) {
                ctx.status(HttpStatus.FORBIDDEN).json(returnObject.put("msg", "Authorization header malformed"));
                return;
            }
            UserDTO verifiedTokenUser = TokenController.verifyToken(token);
            if (verifiedTokenUser == null) {
                ctx.status(HttpStatus.FORBIDDEN).json(returnObject.put("msg", "Invalid User or Token"));
            }
            System.out.println("USER IN AUTHENTICATE: " + verifiedTokenUser);
            ctx.attribute("user", verifiedTokenUser);
        };
    }

    public static boolean authorize(UserDTO user, Set<String> allowedRoles)
    {
        AtomicBoolean hasAccess = new AtomicBoolean(false); // Since we update this in a lambda expression, we need to use an AtomicBoolean
        if (user != null) {
            user.getRoles().stream().forEach(role -> {
                if (allowedRoles.contains(role.toUpperCase())) {
                    hasAccess.set(true);
                }
            });
        }
        return hasAccess.get();
    }
}
