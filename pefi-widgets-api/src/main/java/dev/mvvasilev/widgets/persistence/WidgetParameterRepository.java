package dev.mvvasilev.widgets.persistence;

import dev.mvvasilev.widgets.entity.WidgetParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface WidgetParameterRepository extends JpaRepository<WidgetParameter, Long> {

    Collection<WidgetParameter> findAllByWidgetIdIn(Collection<Long> widgetIds);

    @Modifying
    void deleteAllByWidgetId(Long widgetId);

}
