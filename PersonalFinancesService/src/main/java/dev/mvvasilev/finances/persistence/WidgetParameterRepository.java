package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.entity.WidgetParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface WidgetParameterRepository extends JpaRepository<WidgetParameter, Long> {

    Collection<WidgetParameter> findAllByWidgetIdIn(Collection<Long> widgetIds);

}
