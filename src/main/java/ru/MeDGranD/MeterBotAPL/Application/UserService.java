package ru.MeDGranD.MeterBotAPL.Application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.MeDGranD.MeterBotAPL.Application.Contacts.IncreaseUsersMetrics;
import ru.MeDGranD.MeterBotAPL.Infrastucture.UserReposiroty;
import ru.MeDGranD.MeterBotAPL.Model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    private final UserReposiroty _userReposiroty;
    private final Random _randomGenerator;

    @Autowired
    public UserService(UserReposiroty userReposiroty){
        this._userReposiroty = userReposiroty;
        this._randomGenerator = new Random();
    }

    public Optional<Integer> IncreaseMetrics(IncreaseUsersMetrics userInfo){

        int additionalMetrics = this._randomGenerator.nextInt(31) - 10;

        User user = this._userReposiroty.findByUserName(userInfo.username);

        if(user == null){

            user = new User.UserBuilder()
                    .username(userInfo.username)
                    .metrics(0L)
                    .firstUpdate(LocalDateTime.now())
                    .lastUpdate(LocalDateTime.now().minusDays(1))
                    .telegramId(userInfo.id)
                    .build();

            this._userReposiroty.save(user);

        }

        if(ChronoUnit.DAYS.between(user.getLastUpdate(), LocalDateTime.now()) >= 1){

            user.setMetrics(user.getMetrics() + additionalMetrics);
            user.setLastUpdate(LocalDateTime.now());
            this._userReposiroty.save(user);

            return Optional.of(additionalMetrics);

        }
        else{

            return Optional.empty();

        }

    }

    public Map<String, Long> GetStatistics(){

        Map<String, Long> returnMap = new HashMap<>();

        for(User user : this._userReposiroty.findAll()){
            returnMap.put(user.getUserName(), user.getMetrics());
        }

        return returnMap;

    }

    public User GetUser(String userName){
        return  this._userReposiroty.findByUserName(userName);
    }

    public List<User> getUsers(int limit, int skip){

        return StreamSupport
                .stream(this._userReposiroty.findAll().spliterator(), false)
                .skip(skip)
                .limit(limit)
                .toList();

    }

    public User CreateUser(User newUser){

        if(this._userReposiroty.findByUserName(newUser.getUserName()) != null){
            return null;
        }

        this._userReposiroty.save(newUser);

        return  newUser;

    }

    public void DeleteUser(User UserToDelete){

        if(this._userReposiroty.findByUserName(UserToDelete.getUserName()) == null){
            return;
        }

        this._userReposiroty.delete(UserToDelete);

    }

    public void UpdateUser(User UserToUpdate) {

        if(this._userReposiroty.findByUserName(UserToUpdate.getUserName()) == null){
            return;
        }

        this._userReposiroty.save(UserToUpdate);

    }
}
