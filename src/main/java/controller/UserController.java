package controller;

import dao.UserDAO;
import dtos.UserDTO;
import exceptions.APIException;
import io.javalin.http.Handler;
import persistence.model.User;

import javax.security.auth.callback.CallbackHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class UserController {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String timestamp = dateFormat.format(new Date());

    public static Handler getAllUsers(UserDAO userDAO) {
        return ctx -> {
            Set<User> userList = userDAO.getAll();
            if (userList != null)
            {
                List<UserDTO> userDTOList = new ArrayList<>();
                for (User u : userList)
                {
                    userDTOList.add(new UserDTO(u));
                }
                ctx.json(userDTOList);
            }
            else
            {
                throw new APIException(404, "No users found",timestamp );
            }
        };
    }

    public static Handler getUserById(UserDAO userDAO)
    {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            User foundUser = (User) userDAO.getById(id);
            if (foundUser != null)
            {
                ctx.json(new UserDTO(foundUser));
            }
            else
            {
                throw new APIException(404, "The ID you are looking for doesn't exist", timestamp);
            }
        };
    }

    public static Handler createUser(UserDAO userDAO)
    {
        return ctx -> {
            User user = ctx.bodyAsClass(User.class);
            if (user != null)
            {
                User createdUser = (User) userDAO.create(user);
                ctx.json(new UserDTO(createdUser));
            }
            else
            {
                throw new APIException(500, "Information wasn't filled out correct", timestamp);
            }
        };

    }

    public static Handler updateUser(UserDAO userDAO) {
        return ctx ->{
            int id = Integer.parseInt(ctx.pathParam("id"));
            User user = ctx.bodyAsClass(User.class);
            if(user != null){
                User updatedUser = (User) userDAO.update(user);
                ctx.json(new UserDTO(updatedUser));
            }else {
                throw new APIException(404, "Information wasn't filled out correct", timestamp);
            }
        };
    }
}
