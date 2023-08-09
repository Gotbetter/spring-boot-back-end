package pcrc.gotbetter.user.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.user.service.UserOperationUseCase;
import pcrc.gotbetter.user.service.UserReadUseCase;
import pcrc.gotbetter.user.ui.requestBody.AdminChangeRequest;
import pcrc.gotbetter.user.ui.requestBody.UserJoinRequest;
import pcrc.gotbetter.user.ui.requestBody.UserLoginRequest;
import pcrc.gotbetter.user.ui.requestBody.UserUpdateRequest;
import pcrc.gotbetter.user.ui.requestBody.UserVerifyIdRequest;
import pcrc.gotbetter.user.ui.view.UserView;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

	private final UserOperationUseCase userOperationUseCase;
	private final UserReadUseCase userReadUseCase;

	@Autowired
	public UserController(
		UserOperationUseCase userOperationUseCase,
		UserReadUseCase userReadUseCase
	) {
		this.userOperationUseCase = userOperationUseCase;
		this.userReadUseCase = userReadUseCase;
	}

	@PostMapping(value = "")
	public ResponseEntity<UserView> newUserJoin(@Valid @RequestBody UserJoinRequest request) {

		log.info("\"JOIN\"");

		var command = UserOperationUseCase.UserCreateCommand.builder()
			.authId(request.getAuth_id())
			.password(request.getPassword())
			.username(request.getUsername())
			.email(request.getEmail())
			.build();
		UserReadUseCase.FindUserResult result = userOperationUseCase.createUser(command);

		return ResponseEntity.created(null).body(UserView.builder().userResult(result).build());
	}

	@PostMapping(value = "/verify")
	public ResponseEntity<UserView> verifyId(@Valid @RequestBody UserVerifyIdRequest request) {

		log.info("\"VERIFY ID\"");

		UserReadUseCase.FindUserResult result = userReadUseCase.verifyId(request.getAuth_id());

		return ResponseEntity.ok(UserView.builder().userResult(result).build());
	}

	@PostMapping(value = "/login")
	public ResponseEntity<UserView> login(
		@Valid @RequestBody UserLoginRequest request,
		@RequestParam(name = "admin", required = false) Boolean isAdmin
	) {

		log.info("\"LOGIN\"");

		var query = UserReadUseCase.UserFindQuery.builder()
			.authId(request.getAuth_id())
			.password(request.getPassword())
			.isAdmin(isAdmin != null && isAdmin)
			.build();
		UserReadUseCase.FindUserResult result = userReadUseCase.loginUser(query);

		return ResponseEntity.ok(UserView.builder().userResult(result).build());
	}

	@GetMapping(value = "")
	public ResponseEntity<UserView> getUserInfo() throws IOException {

		log.info("\"GET USER'S INFO\"");

		UserReadUseCase.FindUserResult result = userReadUseCase.getUserInfo();

		return ResponseEntity.ok(UserView.builder().userResult(result).build());
	}

	@GetMapping(value = "/all")
	public ResponseEntity<List<UserView>> getAllUserInfo() throws IOException {

		log.info("\"GET ALL USER'S INFO\"");

		List<UserReadUseCase.FindUserResult> result = userReadUseCase.getAllUserInfo();
		List<UserView> userViews = new ArrayList<>();

		for (UserReadUseCase.FindUserResult findUserResult : result) {
			userViews.add(UserView.builder().userResult(findUserResult).build());
		}
		return ResponseEntity.ok(userViews);
	}

	@PatchMapping(value = "/{user_id}/admin")
	public void changeAuthentication(
		@PathVariable(value = "user_id") Long userId,
		@Valid @RequestBody AdminChangeRequest request
	) {

		log.info("\"CHANGE USER AUTHENTICATION\"");

		var command = UserOperationUseCase.UserAdminUpdateCommand.builder()
			.userId(userId)
			.approve(request.getApprove())
			.build();
		userOperationUseCase.changeAuthentication(command);
	}

	@DeleteMapping(value = "/{user_id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable(value = "user_id") Long userId) {

		log.info("\"DELETE USER\"");

		userOperationUseCase.deleteUser(userId);
	}

	@PostMapping(value = "/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(@RequestParam(name = "admin", required = false) Boolean isAdmin) {

		log.info("\"LOGOUT\"");

		userReadUseCase.logoutUser(isAdmin);
	}

	@PatchMapping(value = "/{user_id}")
	public void updateUser(
		@PathVariable(value = "user_id") Long userId,
		@Valid @RequestBody UserUpdateRequest request
	) {

		log.info("\"UPDATE USER INFO\"");

		var command = UserOperationUseCase.UserUpdateCommand.builder()
			.userId(userId)
			.username(request.getUsername())
			.build();

		userOperationUseCase.updateUserInfo(command);
	}

}
