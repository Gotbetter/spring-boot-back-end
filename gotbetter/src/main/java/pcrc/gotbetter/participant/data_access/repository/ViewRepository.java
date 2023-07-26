package pcrc.gotbetter.participant.data_access.repository;

import static pcrc.gotbetter.participant.data_access.view.QEnteredView.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import pcrc.gotbetter.participant.data_access.view.EnteredView;

@Repository
public class ViewRepository {
	private final JPAQueryFactory queryFactory;

	public ViewRepository(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	public List<EnteredView> enteredListByRoomId(Long roomId) {
		return queryFactory
			.selectFrom(enteredView)
			.where(enteredView.roomId.eq(roomId))
			.fetch();
	}

	public Boolean enteredExistByUserIdRoomId(Long userId, Long roomId) {
		Integer exists = queryFactory
			.selectOne()
			.from(enteredView)
			.where(enteredView.userId.eq(userId),
				enteredView.roomId.eq(roomId))
			.fetchFirst();
		return exists != null;
	}
}
