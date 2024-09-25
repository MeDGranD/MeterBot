package ru.MeDGranD.MeterBotAPL;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.MeDGranD.MeterBotAPL.Infrastucture.UserReposiroty;
import ru.MeDGranD.MeterBotAPL.Model.User;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Testcontainers
@SpringBootTest
class UserRepositoryTests {

    @Container
    @ServiceConnection
    private final static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(PostgreSQLContainer.IMAGE);

    @Autowired
    private UserReposiroty _userReposiroty;

    @Test
    public void testFindByUsername_UserNotExists(){

        User user = _userReposiroty.findByUserName("user");
        assertNull(user);

    }

    @Test
    public void testFindByUsername_UserExists(){

        User user = new User.UserBuilder()
                .username("username")
                .build();

        _userReposiroty.save(user);

        User foundUser = _userReposiroty.findByUserName("username");
        assertNotNull(foundUser);

    }

}
