package pcrc.gotbetter.participant.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.participant.data_access.view.EnteredView;
import pcrc.gotbetter.participant.data_access.view.TryEnterView;

import java.util.List;

import static pcrc.gotbetter.participant.data_access.view.QEnteredView.enteredView;
import static pcrc.gotbetter.participant.data_access.view.QTryEnterView.tryEnterView;

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

    public EnteredView enteredByUserIdRoomId(Long userId, Long roomId) {
        return queryFactory
                .selectFrom(enteredView)
                .where(enteredView.userId.eq(userId),
                        enteredView.roomId.eq(roomId))
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

    public TryEnterView tryEnterByUserIdRoomId(Long userId, Long roomId, Boolean accepted) {
        return queryFactory
                .selectFrom(tryEnterView)
                .where(tryEnterViewEqUserId(userId),
                        tryEnterViewEqRoomId(roomId),
                        tryEnterViewEqAccepted(accepted))
                .fetchFirst();
    }

    public List<TryEnterView> tryEnterListByUserIdRoomId(Long userId, Long roomId, Boolean accepted) {
        return queryFactory
                .selectFrom(tryEnterView)
                .where(tryEnterViewEqUserId(userId),
                        tryEnterViewEqRoomId(roomId),
                        tryEnterViewEqAccepted(accepted))
                .fetch();
    }

    /**
     * try-enter view eq
     */
    private BooleanExpression tryEnterViewEqUserId(Long userId) {
        return userId == null ? null : tryEnterView.tryEnterId.userId.eq(userId);
    }
    private BooleanExpression tryEnterViewEqRoomId(Long roomId) {
        return roomId == null ? null : tryEnterView.tryEnterId.roomId.eq(roomId);
    }
    private BooleanExpression tryEnterViewEqAccepted(Boolean accepted) {
        return accepted == null ? null : tryEnterView.accepted.eq(accepted);
    }
}
