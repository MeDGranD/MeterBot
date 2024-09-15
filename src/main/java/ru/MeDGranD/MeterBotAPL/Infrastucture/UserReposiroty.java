package ru.MeDGranD.MeterBotAPL.Infrastucture;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.MeDGranD.MeterBotAPL.Model.User;

@Repository
public interface UserReposiroty extends CrudRepository<User, Long> {

    User findByUserName(String UserName);

}
