package dev.mvvasilev.finances.entity;

import dev.mvvasilev.common.data.AbstractEntity;
import dev.mvvasilev.common.data.UserOwned;
import dev.mvvasilev.finances.enums.CategorizationRuleBehavior;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(schema = "categories")
public class TransactionCategory extends AbstractEntity implements UserOwned {

    private Integer userId;

    private String name;

    @Convert(converter = CategorizationRuleBehavior.JpaConverter.class)
    private CategorizationRuleBehavior ruleBehavior;

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

    public CategorizationRuleBehavior getRuleBehavior() {
        return ruleBehavior;
    }

    public void setRuleBehavior(CategorizationRuleBehavior ruleBehavior) {
        this.ruleBehavior = ruleBehavior;
    }
}
