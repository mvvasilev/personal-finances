package dev.mvvasilev.finances.entity;

import dev.mvvasilev.common.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(schema = "categories")
public class TransactionCategory extends AbstractEntity {

    private Integer userId;

    private String name;

    public TransactionCategory() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
