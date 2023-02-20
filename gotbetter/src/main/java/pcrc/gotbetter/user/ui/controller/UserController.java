package pcrc.gotbetter.user.ui.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pcrc.gotbetter.user.service.UserReadUseCase;
import pcrc.gotbetter.user.ui.requestBody.UserJoinRequest;
import pcrc.gotbetter.user.service.UserOperationUseCase;
import pcrc.gotbetter.user.ui.requestBody.UserLoginRequest;
import pcrc.gotbetter.user.ui.requestBody.UserVerifyIdRequest;
import pcrc.gotbetter.user.ui.view.UserView;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserOperationUseCase userOperationUseCase;
    private final UserReadUseCase userReadUseCase;

    @Autowired
    public UserController(UserOperationUseCase userOperationUseCase,
                          UserReadUseCase userReadUseCase) {
        this.userOperationUseCase = userOperationUseCase;
        this.userReadUseCase = userReadUseCase;
    }

    @PostMapping(value = "")
    public ResponseEntity<UserView> newUserJoin(@Valid @RequestBody UserJoinRequest request) {

        log.info("\"JOIN\"");

        var command = UserOperationUseCase.UserCreateCommand.builder()
                .auth_id(request.getAuth_id())
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
    public ResponseEntity<UserView> login(@Valid @RequestBody UserLoginRequest request) throws IOException {

        log.info("\"LOGIN\"");

        var query = UserReadUseCase.UserFindQuery.builder()
                .auth_id(request.getAuth_id())
                .password(request.getPassword())
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
}
