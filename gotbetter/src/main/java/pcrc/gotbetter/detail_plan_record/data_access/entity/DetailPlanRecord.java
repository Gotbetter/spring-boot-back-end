package pcrc.gotbetter.detail_plan_record.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.gotbetter.setting.BaseTimeEntity;

@Entity
@Table(name = "detail_plan_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class DetailPlanRecord extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "record_id", nullable = false)
	private Long recordId;
	@Embedded
	DetailPlanId detailPlanId;
	@Column(name = "record_title", nullable = false)
	private String recordTitle;
	@Column(name = "record_body", nullable = false)
	private String recordBody;
	@Column(name = "record_photo", nullable = false)
	private String recordPhoto;

	@Builder
	public DetailPlanRecord(Long recordId, DetailPlanId detailPlanId,
		String recordTitle, String recordBody, String recordPhoto) {
		this.recordId = recordId;
		this.detailPlanId = detailPlanId;
		this.recordTitle = recordTitle;
		this.recordBody = recordBody;
		this.recordPhoto = recordPhoto;
	}

	public void updateRecord(String recordTitle, String recordBody, String recordPhoto) {
		this.recordTitle = recordTitle;
		this.recordBody = recordBody;
		this.recordPhoto = recordPhoto;
	}
}
