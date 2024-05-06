package controllers;

import daos.EventDAO;
import daos.UserDAO;
import dtos.UserDTO;
import exceptions.APIException;
import io.javalin.http.Handler;
import persistence.model.Event;
import persistence.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserController {


    public static Handler addUserToEvent(UserDAO userDAO, EventDAO eventDAO) {
        return ctx ->{
            int eventId = Integer.parseInt(ctx.pathParam("eventid"));
            int userid = Integer.parseInt(ctx.pathParam("userid"));
            //int userId = ctx.bodyAsClass(Integer.class);

            User user = (User) userDAO.getById(userid);
            Event event = (Event) eventDAO.getById(eventId);

            if(user != null && event != null) {
                user.addEvent(event);
                userDAO.update(user);
                ctx.json(user);
            }else{
                throw new APIException(404, "Registration of user to event was not possible, User og Event was not found ");
            }
        };
    }
    public static Handler getAllUsers(UserDAO userDAO) {
        return ctx -> {
            List<User> userList = userDAO.getAllUsers();
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
                throw new APIException(404, "No users found");
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
                throw new APIException(404, "The ID you are looking for doesn't exist");
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
                throw new APIException(500, "Information wasn't filled out correct");
            }
        };
    }

    public static Handler updateUser(UserDAO userDAO)
    {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            User updatedUser = ctx.bodyAsClass(User.class);
            User foundUser = (User) userDAO.getById(id);
            if (foundUser != null)
            {
                if (updatedUser.getName() != null && !updatedUser.getName().isEmpty())
                {
                    foundUser.setName(updatedUser.getName());
                }
                if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty())
                {
                    foundUser.setEmail(updatedUser.getEmail());
                }
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty())
                {
                    foundUser.setPassword(updatedUser.getPassword());
                }
                if (updatedUser.getPhone() != 0)
                {
                    foundUser.setPhone(updatedUser.getPhone());
                }
                userDAO.update(foundUser);
                ctx.json(new UserDTO(foundUser));
            }
            else
            {
                throw new APIException(404,"Couldn't find the user with the given ID or the information was written incorrectly");
            }
        };
    }

    public static Handler deleteUser(UserDAO userDAO)
    {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            User foundUser = (User) userDAO.getById(id);
            int deletedUser = userDAO.delete(id);
            if (deletedUser != 0)
            {
                ctx.json(new UserDTO(foundUser));
            }
            else
            {
                throw new APIException(404, "There are no user with the given ID");
            }

        };
    }
}
