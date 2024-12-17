package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.Pending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingRepository extends JpaRepository<Pending, Long> {
}
