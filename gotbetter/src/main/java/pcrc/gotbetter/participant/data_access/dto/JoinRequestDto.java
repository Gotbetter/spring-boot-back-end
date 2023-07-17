package pcrc.gotbetter.participant.data_access.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pcrc.gotbetter.participant.data_access.entity.JoinRequest;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.entity.UserSet;

@Getter
@AllArgsConstructor
public class JoinRequestDto {
	private JoinRequest joinRequest;
	private Room room;
	private User user;
	private UserSet userSet;

	// 생성자, getter, setter 생략

	// 필요한 경우 다른 생성자도 추가할 수 있습니다.
}
