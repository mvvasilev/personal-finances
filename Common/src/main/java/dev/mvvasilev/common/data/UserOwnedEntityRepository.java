package dev.mvvasilev.common.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface UserOwnedEntityRepository<T, ID> extends JpaRepository<T, ID> {

    Page<T> findAllByUserId(int userId, Pageable pageable);

    Collection<T> findAllByUserId(int userId);

}
