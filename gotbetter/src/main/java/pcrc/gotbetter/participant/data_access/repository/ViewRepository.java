package pcrc.gotbetter.participant.data_access.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.participant.data_access.view.EnteredView;

import java.util.List;

import static pcrc.gotbetter.participant.data_access.view.QEnteredView.enteredView;

@Repository
public class ViewRepository {
    private final JPAQueryFactory queryFactory;

    public ViewRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public EnteredView enteredByParticipantId(Long participantId) {
        return queryFactory
                .selectFrom(enteredView)
                .where(enteredView.participantId.eq(participantId))
                .fetchFirst();
    }

    public List<EnteredView> enteredListByRoomId(Long roomId) {
        return queryFactory
                .selectFrom(enteredView)
                .where(enteredView.roomId.eq(roomId))
                .fetch();
    }

    public Boolean enteredExistByUserIdRoomId(Long userId, Long roomId) {
        Integer exists =  queryFactory
                .selectOne()
                .from(enteredView)
                .where(enteredView.userId.eq(userId),
                        enteredView.roomId.eq(roomId))
                .fetchFirst();
        return exists != null;
    }
}
