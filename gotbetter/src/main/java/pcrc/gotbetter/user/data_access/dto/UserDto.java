package pcrc.gotbetter.user.data_access.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.entity.UserSet;

@Data
@NoArgsConstructor
@Builder
public class UserDto {
	private User user;
	private UserSet userSet;

	public UserDto(User user, UserSet userSet) {
		this.user = user;
		this.userSet = userSet;
	}
}