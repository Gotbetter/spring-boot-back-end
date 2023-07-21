package pcrc.gotbetter.detail_plan_record.data_access.repository;

import pcrc.gotbetter.detail_plan_record.data_access.dto.DetailPlanRecordDto;

public interface DetailPlanRecordQueryRepository {

	DetailPlanRecordDto findDetailPlanJoinRecord(Long recordId);

}
