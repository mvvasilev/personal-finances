package dev.mvvasilev.statements.persistence;

import dev.mvvasilev.statements.entity.RawStatement;
import dev.mvvasilev.statements.persistence.dto.RawStatementDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RawStatementRepository extends JpaRepository<RawStatement, Long> {

    @Query(value =
            "SELECT " +
                "id, " +
                "time_created as TimeCreated, " +
                "time_last_modified as TimeLastModified, " +
                "user_id as UserId, name " +
            "FROM transactions.raw_statement " +
            "WHERE user_id = :userId",
            nativeQuery = true
    )
    Collection<RawStatementDTO> fetchAllForUser(@Param("userId") int userId);

}
