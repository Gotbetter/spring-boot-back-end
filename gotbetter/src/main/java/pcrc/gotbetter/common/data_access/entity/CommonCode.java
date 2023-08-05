package pcrc.gotbetter.common.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.gotbetter.setting.common.BaseTimeEntity;

@Entity
@Table(name = "CommonCode")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class CommonCode extends BaseTimeEntity {
	@EmbeddedId
	private CommonCodeId commonCodeId;
	@Column(name = "code_description")
	private String codeDescription;
	private String attribute1;
	private String attribute2;
	private String attribute3;
	private Integer order;

	@Builder
	public CommonCode(
		CommonCodeId commonCodeId,
		String codeDescription,
		String attribute1,
		String attribute2,
		String attribute3,
		Integer order
	) {
		this.commonCodeId = commonCodeId;
		this.codeDescription = codeDescription;
		this.attribute1 = attribute1;
		this.attribute2 = attribute2;
		this.attribute3 = attribute3;
		this.order = order;
	}

	public void changeImageToByte(String bytes) {
		this.attribute1 = bytes;
	}

	public void updateInfo(String codeDescription, String attribute1, String attribute2) {
		this.codeDescription = codeDescription;
		this.attribute1 = attribute1;
		this.attribute2 = attribute2;
	}
}
