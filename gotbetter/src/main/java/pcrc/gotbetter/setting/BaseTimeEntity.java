package pcrc.gotbetter.setting;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "created_date", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @CreatedBy
    @Column(name = "created_by_id", updatable = false, nullable = false)
    private String createdById;

    @LastModifiedBy
    @Column(name = "updated_by_id")
    private String updatedById;

    @PrePersist
    public void updateCreatedDate() {
        this.createdDate = LocalDateTime.now();
        try {
            this.createdById = getCurrentUserId().toString();
        } catch (Exception e) {}
    }

    @PreUpdate
    public void updateUpdatedDate() {
        this.updatedDate = LocalDateTime.now();
        try {
            this.updatedById = getCurrentUserId().toString();
        } catch (Exception e) {}
    }

    public void updateCreatedById(String userId) {
        this.createdById = userId;
    }

    public void updateUpdatedById(String userId) {
        this.updatedById = userId;
    }
}
