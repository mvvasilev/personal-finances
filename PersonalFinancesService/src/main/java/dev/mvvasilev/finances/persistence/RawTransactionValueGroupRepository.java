package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.entity.RawTransactionValueGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawTransactionValueGroupRepository extends JpaRepository<RawTransactionValueGroup, Long> {
}
