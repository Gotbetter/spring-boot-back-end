package pcrc.gotbetter.user.login_method.login_type;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum RoleType {
	USER("ROLE_USER", "일반 사용자 권한"),
	MAIN_ADMIN("ROLE_MAIN_ADMIN", "메인 관리자 권한"),
	ADMIN("ROLE_ADMIN", "관리자 권한"),
	SERVER("ROLE_SERVER", "서버 권한"),
	GUEST("GUEST", "게스트 권한");

	private final String code;
	private final String displayName;

	RoleType(String code, String displayName) {
		this.code = code;
		this.displayName = displayName;
	}

	public static RoleType of(String code) {
		return Arrays.stream(RoleType.values())
			.filter(role -> role.getCode().equals(code))
			.findAny()
			.orElse(GUEST);
	}
}
