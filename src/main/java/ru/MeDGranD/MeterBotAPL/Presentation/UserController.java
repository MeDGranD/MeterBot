package ru.MeDGranD.MeterBotAPL.Presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.MeDGranD.MeterBotAPL.Application.UserService;
import ru.MeDGranD.MeterBotAPL.Model.User;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserService _userService;

    @Autowired
    public UserController(UserService userService){
        this._userService = userService;
    }

    @GetMapping("/users/{userName}")
    public User GetUser(String userName){
        return this._userService.GetUser(userName);
    }

    @GetMapping("/users")
    public List<User> GetAllUsers(@RequestParam(value = "limit", required = false, defaultValue = "2147483647") int limit,
                                            @RequestParam(value = "skip", required = false, defaultValue = "0") int skip){
        return this._userService.getUsers(limit, skip);
    }

    @GetMapping("/users/statistics")
    public Map<String, Long> GetStatistics(){
        return this._userService.GetStatistics();
    }

    @PostMapping("/users")
    public User CreateUser(User newUser){
        return this._userService.CreateUser(newUser);
    }

    @DeleteMapping("/users")
    public void DeleteUser(User UserToDelete){
        this._userService.DeleteUser(UserToDelete);
    }

    @DeleteMapping("/users/{userName}")
    public void DeleteUserViaUserName(String userName){
        this._userService.DeleteUser(
                this._userService.GetUser(userName)
        );
    }

    @PutMapping("/users")
    public void UpdateUser(User UserToUpdate){
        this._userService.UpdateUser(UserToUpdate);
    }

}
