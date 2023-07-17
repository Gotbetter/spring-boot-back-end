package pcrc.gotbetter.setting;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime created_date;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updated_date;

    @PrePersist
    public void updateCreatedDate() {
        this.created_date = LocalDateTime.now();
    }

    @PreUpdate
    public void updateUpdatedDate() {
        this.updated_date = LocalDateTime.now();
    }

    // post , pre
    // created_by, updated_by
}
