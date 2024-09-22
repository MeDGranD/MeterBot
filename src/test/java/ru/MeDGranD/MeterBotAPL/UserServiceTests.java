package ru.MeDGranD.MeterBotAPL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.MeDGranD.MeterBotAPL.Application.Contacts.IncreaseUsersMetrics;
import ru.MeDGranD.MeterBotAPL.Application.UserService;
import ru.MeDGranD.MeterBotAPL.Infrastucture.UserReposiroty;
import ru.MeDGranD.MeterBotAPL.Model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTests {

	@Mock
	private UserReposiroty _userRepository;

	@InjectMocks
	private UserService _userService;

	@Test
	public void testGetUserByUsername_userExists(){

		String username = "user";
		User user = new User.UserBuilder()
				.username(username)
				.build();

		when(_userRepository.findByUserName(username)).thenReturn(user);

		User foundUser = _userService.GetUser(username);

		assertNotNull(foundUser);
		assertEquals(username, foundUser.getUserName());
		verify(_userRepository, times(1)).findByUserName(username);

	}

	@Test
	public void testGetUserByUsername_userNotExists(){

		String username = "user";

		when(_userRepository.findByUserName(username)).thenReturn(null);

		User foundUser = _userService.GetUser(username);

		assertNull(foundUser);
		verify(_userRepository, times(1)).findByUserName(username);

	}

	@Test
	public void testIncreaseUserMetrics_userExistsWithinDayDuration(){

		String username = "user";
		User user = new User.UserBuilder()
				.username(username)
				.lastUpdate(LocalDateTime.now().minusHours(2))
				.build();

		when(_userRepository.findByUserName(username)).thenReturn(user);

		IncreaseUsersMetrics request = new IncreaseUsersMetrics();
		request.username = username;

		Optional<Integer> metrics = _userService.IncreaseMetrics(request);

		assertTrue(metrics.isEmpty());
		verify(_userRepository, times(1)).findByUserName(username);

	}

	@Test
	public void testIncreaseUserMetrics_userExistsBeyondDayDuration(){

		String username = "user";
		User user = new User.UserBuilder()
				.username(username)
				.lastUpdate(LocalDateTime.now().minusDays(2))
				.metrics(5L)
				.build();

		when(_userRepository.findByUserName(username)).thenReturn(user);

		IncreaseUsersMetrics request = new IncreaseUsersMetrics();
		request.username = username;

		Optional<Integer> metrics = _userService.IncreaseMetrics(request);

		assertTrue(metrics.isPresent());
		verify(_userRepository, times(1)).findByUserName(username);

	}

	@Test
	public void testIncreaseUserMetrics_userExistsWithDayDuration(){

		String username = "user";
		User user = new User.UserBuilder()
				.username(username)
				.lastUpdate(LocalDateTime.now().minusDays(1))
				.metrics(5L)
				.build();

		when(_userRepository.findByUserName(username)).thenReturn(user);

		IncreaseUsersMetrics request = new IncreaseUsersMetrics();
		request.username = username;

		Optional<Integer> metrics = _userService.IncreaseMetrics(request);

		assertTrue(metrics.isPresent());
		verify(_userRepository, times(1)).findByUserName(username);

	}

	@Test
	public void testIncreaseUserMetrics_userNotExists(){

		String username = "user";

		when(_userRepository.findByUserName(username)).thenReturn(null);

		IncreaseUsersMetrics request = new IncreaseUsersMetrics();
		request.username = username;

		Optional<Integer> metrics = _userService.IncreaseMetrics(request);

		assertTrue(metrics.isPresent());
		verify(_userRepository, times(1)).findByUserName(username);

	}

	@Test
	public void testGetStatistics_noUsers(){

		when(_userRepository.findAll()).thenReturn(List.of());

		Map<String, Long> map = _userService.GetStatistics();

		assertTrue(map.isEmpty());
		verify(_userRepository, times(1)).findAll();

	}

	@Test
	public void testGetStatistics_withOneUser(){

		User user = new User.UserBuilder()
				.username("user")
				.metrics(1L)
				.build();

		when(_userRepository.findAll()).thenReturn(List.of(user));

		Map<String, Long> map = _userService.GetStatistics();

		assertFalse(map.isEmpty());
		assertEquals(user.getMetrics(), map.get(user.getUserName()));
		verify(_userRepository, times(1)).findAll();

	}

	@Test
	public void testGetStatistics_with150Users(){

		List<User> testTemplates = new ArrayList<>();

		for(int i = 0; i < 150; ++i) {
			User user = new User.UserBuilder()
					.username("user" + i)
					.metrics((long)i)
					.build();
			testTemplates.add(user);
		}

		when(_userRepository.findAll()).thenReturn(testTemplates);

		Map<String, Long> map = _userService.GetStatistics();

		assertFalse(map.isEmpty());

		for(int i = 0; i < 150; ++i) {
			assertEquals(testTemplates.get(i).getMetrics(), map.get("user" + i));
		}

		verify(_userRepository, times(1)).findAll();

	}

	@Test
	public void testCreateUser_userNotExists(){

		User newUser = new User.UserBuilder()
				.username("user")
				.build();

		when(_userRepository.findByUserName("user")).thenReturn(null);

		User createdUser = _userService.CreateUser(newUser);

		assertEquals(newUser, createdUser);
		verify(_userRepository, times(1)).save(newUser);

	}

	@Test
	public void testCreateUser_userExists(){

		User newUser = new User.UserBuilder()
				.username("user")
				.build();

		when(_userRepository.findByUserName("user")).thenReturn(newUser);

		User createdUser = _userService.CreateUser(newUser);

		assertNull(createdUser);
		verify(_userRepository, times(0)).save(newUser);

	}

	@Test
	public void testDeleteUser_userNotExists(){

		User deleteUser = new User.UserBuilder()
				.username("user")
				.build();

		when(_userRepository.findByUserName("user")).thenReturn(null);

		_userService.DeleteUser(deleteUser);

		verify(_userRepository, times(0)).delete(deleteUser);

	}

	@Test
	public void testDeleteUser_userExists(){

		User deleteUser = new User.UserBuilder()
				.username("user")
				.build();

		when(_userRepository.findByUserName("user")).thenReturn(deleteUser);

		_userService.DeleteUser(deleteUser);

		verify(_userRepository, times(1)).delete(deleteUser);

	}

	@Test
	public void testUpdateUser_userNotExists(){

		User updateUser = new User.UserBuilder()
				.username("user")
				.build();

		when(_userRepository.findByUserName("user")).thenReturn(null);

		_userService.UpdateUser(updateUser);

		verify(_userRepository, times(0)).save(updateUser);

	}

	@Test
	public void testUpdateUser_userExists(){

		User updateUser = new User.UserBuilder()
				.username("user")
				.build();

		when(_userRepository.findByUserName("user")).thenReturn(updateUser);

		_userService.UpdateUser(updateUser);

		verify(_userRepository, times(1)).save(updateUser);

	}

	@Test
	public void testGetUsers_with150Users_noOffsetNoLimit(){

		List<User> testTemplates = new ArrayList<>();

		for(int i = 0; i < 150; ++i) {
			User user = new User.UserBuilder()
					.username("user" + i)
					.metrics((long)i)
					.build();
			testTemplates.add(user);
		}

		when(_userRepository.findAll()).thenReturn(testTemplates);

		List<User> userList = _userService.getUsers(Integer.MAX_VALUE, 0);

        assertEquals(testTemplates, userList);
		verify(_userRepository, times(1)).findAll();

	}

	@Test
	public void testGetUsers_withNoUsers(){

		when(_userRepository.findAll()).thenReturn(List.of());

		List<User> userList = _userService.getUsers(Integer.MAX_VALUE, 0);

		assertTrue(userList.isEmpty());
		verify(_userRepository, times(1)).findAll();

	}

	@Test
	public void testGetUsers_with150Users_noOffsetWithLimit(){

		List<User> testTemplates = new ArrayList<>();

		for(int i = 0; i < 150; ++i) {
			User user = new User.UserBuilder()
					.username("user" + i)
					.metrics((long)i)
					.build();
			testTemplates.add(user);
		}

		when(_userRepository.findAll()).thenReturn(testTemplates);

		List<User> userList = _userService.getUsers(50, 0);

        assertEquals(50, userList.size());
		assertEquals(testTemplates.stream().limit(50).toList(), userList);
		verify(_userRepository, times(1)).findAll();

	}

	@Test
	public void testGetUsers_with150Users_withOffsetNoLimit(){

		List<User> testTemplates = new ArrayList<>();

		for(int i = 0; i < 150; ++i) {
			User user = new User.UserBuilder()
					.username("user" + i)
					.metrics((long)i)
					.build();
			testTemplates.add(user);
		}

		when(_userRepository.findAll()).thenReturn(testTemplates);

		List<User> userList = _userService.getUsers(Integer.MAX_VALUE, 50);

		assertEquals(100, userList.size());
		assertEquals(testTemplates.stream().skip(50).toList(), userList);
		verify(_userRepository, times(1)).findAll();

	}

	@Test
	public void testGetUsers_with150Users_withOffsetWithLimit(){

		List<User> testTemplates = new ArrayList<>();

		for(int i = 0; i < 150; ++i) {
			User user = new User.UserBuilder()
					.username("user" + i)
					.metrics((long)i)
					.build();
			testTemplates.add(user);
		}

		when(_userRepository.findAll()).thenReturn(testTemplates);

		List<User> userList = _userService.getUsers(50, 50);

		assertEquals(50, userList.size());
		assertEquals(testTemplates.stream().skip(50).limit(50).toList(), userList);
		verify(_userRepository, times(1)).findAll();

	}

}
