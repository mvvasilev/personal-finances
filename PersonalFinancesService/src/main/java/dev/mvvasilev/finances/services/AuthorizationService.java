package dev.mvvasilev.finances.services;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("authService")
public class AuthorizationService {

    final private EntityManager entityManager;

    @Autowired
    public AuthorizationService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    /**
     * Check if the user in the {@link SecurityContextHolder} is the owner of the requested resource.
     * Executes a query on the database, equivalent to the following:
     * <br>
     * <pre type="SQL">
     *     SELECT 1 FROM {user owned entity} WHERE id = :id AND user_id = :userId
     * </pre>
     *
     * @param id the id of the resource to check
     * @param userOwnedEntity the entity class
     * @return whether the user is the owner of the resource or not
     */
    public boolean isOwner(Long id, Class<?> userOwnedEntity) {
        var cb = entityManager.getCriteriaBuilder();

        var query = cb.createQuery(Boolean.class);
        var root = query.from(userOwnedEntity);

        var userId = SecurityContextHolder.getContext().getAuthentication().getName();

        var parsedUserId = Integer.parseInt(userId);

        var finalQuery = query.select(cb.literal(true)).where(
                cb.and(
                        cb.equal(root.get("id"), id),
                        cb.equal(root.get("userId"), parsedUserId)
                )
        );

        // If no results were returned, then the user was not the owner of the resource
        return !entityManager.createQuery(finalQuery).setMaxResults(1).getResultList().isEmpty();
    }

}
