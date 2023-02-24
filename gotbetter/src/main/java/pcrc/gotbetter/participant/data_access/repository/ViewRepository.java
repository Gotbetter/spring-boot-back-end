package pcrc.gotbetter.participant.data_access.repository;

import com.querydsl.core.BooleanBuilder;
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

    public EnteredView enteredByParticipantId(Long participant_id) {
        return queryFactory
                .selectFrom(enteredView)
                .where(enteredView.participantId.eq(participant_id))
                .fetchFirst();
    }

    public List<EnteredView> enteredListByRoomId(Long room_id) {
        return queryFactory
                .selectFrom(enteredView)
                .where(enteredView.roomId.eq(room_id))
                .fetch();
    }

    public TryEnterView tryEnterByUserIdRoomId(Long user_id, Long room_id, Boolean accepted) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(tryEnterViewEqUserId(user_id))
                .and(tryEnterViewEqRoomId(room_id))
                .and(tryEnterViewEqAccepted(accepted));
        return queryFactory
                .selectFrom(tryEnterView)
                .where(builder)
                .fetchFirst();
    }

    public List<TryEnterView> tryEnterListByUserIdRoomId(Long user_id, Long room_id, Boolean accepted) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(tryEnterViewEqUserId(user_id))
                .and(tryEnterViewEqRoomId(room_id))
                .and(tryEnterViewEqAccepted(accepted));
        return queryFactory
                .selectFrom(tryEnterView)
                .where(builder)
                .fetch();
    }

    /**
     * try-enter view eq
     */
    private BooleanExpression tryEnterViewEqUserId(Long user_id) {
        return user_id == null ? null : tryEnterView.userId.eq(user_id);
    }
    private BooleanExpression tryEnterViewEqRoomId(Long room_id) {
        return room_id == null ? null : tryEnterView.roomId.eq(room_id);
    }
    private BooleanExpression tryEnterViewEqAccepted(Boolean accepted) {
        return accepted == null ? null : tryEnterView.accepted.eq(accepted);
    }
}
