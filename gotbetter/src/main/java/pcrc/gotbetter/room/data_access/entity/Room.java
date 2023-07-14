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
    @Column(name = "room_id")
    private Long roomId;
    private String title;
    @Column(name = "max_user_num")
    private Integer maxUserNum;
    @Column(name = "current_user_num")
    private Integer currentUserNum;
    @Column(name = "start_date")
    private LocalDate startDate;
    private Integer week;
    @Column(name = "current_week")
    private Integer currentWeek;
    @Column(name = "entry_fee")
    private Integer entryFee;
    @Column(name = "room_code")
    private String roomCode;
    private String account;
    @Column(name = "room_category")
    private String roomCategory;
    private String description;
    @Column(name = "total_entry_fee")
    private Integer totalEntryFee;
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
}