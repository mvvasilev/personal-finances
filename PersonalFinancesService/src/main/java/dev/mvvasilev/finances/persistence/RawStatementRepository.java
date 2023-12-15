package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.entity.RawStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawStatementRepository extends JpaRepository<RawStatement, Long> {
}
