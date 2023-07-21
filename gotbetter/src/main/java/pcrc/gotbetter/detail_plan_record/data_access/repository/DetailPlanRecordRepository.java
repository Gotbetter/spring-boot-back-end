package pcrc.gotbetter.detail_plan_record.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pcrc.gotbetter.detail_plan_record.data_access.entity.DetailPlanId;
import pcrc.gotbetter.detail_plan_record.data_access.entity.DetailPlanRecord;

@Repository
public interface DetailPlanRecordRepository extends JpaRepository<DetailPlanRecord, DetailPlanId> {

}
