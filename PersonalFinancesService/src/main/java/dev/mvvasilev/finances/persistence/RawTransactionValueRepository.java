package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.entity.RawTransactionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawTransactionValueRepository extends JpaRepository<RawTransactionValue, Long> {
}
