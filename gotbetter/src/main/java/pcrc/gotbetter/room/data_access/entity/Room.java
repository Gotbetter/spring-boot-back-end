package pcrc.gotbetter.room.data_access.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.gotbetter.setting.BaseTimeEntity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Table(name = "Room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class Room extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
    private Long roomId;
    @Column(nullable = false)
    private String title;
    @Column(name = "max_user_num", nullable = false)
    private Integer maxUserNum;
    @Column(name = "current_user_num", nullable = false)
    private Integer currentUserNum;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private Integer week;
    @Column(name = "current_week", nullable = false)
    private Integer currentWeek;
    @Column(name = "entry_fee", nullable = false)
    private Integer entryFee;
    @Column(name = "room_code", nullable = false)
    private String roomCode;
    @Column(nullable = false)
    private String account;
    @Column(name = "room_category", nullable = false)
    private String roomCategory;
    private String description;
    @Column(name = "total_entry_fee", nullable = false)
    private Integer totalEntryFee;
    @Column(nullable = false)
    private String rule;

    @Builder
    public Room(Long roomId, String title, Integer maxUserNum, Integer currentUserNum,
                LocalDate startDate, Integer week, Integer currentWeek,
                Integer entryFee, String roomCode, String account, String roomCategory,
                String description, Integer totalEntryFee, String rule) {
        this.roomId = roomId;
        this.title = title;
        this.maxUserNum = maxUserNum;
        this.currentUserNum = currentUserNum;
        this.startDate = startDate;
        this.week = week;
        this.currentWeek = currentWeek;
        this.entryFee = entryFee;
        this.roomCode = roomCode;
        this.roomCategory = roomCategory;
        this.account = account;
        this.description = description;
        this.totalEntryFee = totalEntryFee;
        this.rule = rule;
    }

    public void updateCurrentWeekToNext() {
        this.currentWeek += 1;
    }

    public void updateTotalEntryFeeAndCurrentUserNum(Integer fee) {
        this.totalEntryFee += fee;
        this.currentUserNum += 1;
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}