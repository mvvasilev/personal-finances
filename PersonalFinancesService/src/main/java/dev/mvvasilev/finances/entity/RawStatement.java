package dev.mvvasilev.finances.entity;

import dev.mvvasilev.common.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(schema = "transactions")
public class RawStatement extends AbstractEntity {

    private Integer userId;

    public RawStatement() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
