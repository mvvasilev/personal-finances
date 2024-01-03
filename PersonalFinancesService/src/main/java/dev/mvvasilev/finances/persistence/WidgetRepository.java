package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.common.data.UserOwnedEntityRepository;
import dev.mvvasilev.finances.entity.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WidgetRepository extends UserOwnedEntityRepository<Widget, Long> {
}
