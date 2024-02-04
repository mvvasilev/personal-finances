package dev.mvvasilev.finances;

import dev.mvvasilev.finances.entity.Categorization;
import dev.mvvasilev.finances.enums.CategorizationRule;
import dev.mvvasilev.finances.enums.ProcessedTransactionField;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;

public class CategorizationBuilder {

    private Categorization categorization;

    public CategorizationBuilder() {}

    private CategorizationBuilder(Categorization categorization) {
        this.categorization = categorization;
    }

    public static CategorizationBuilder withUser(int userId) {
        var categorization = new Categorization();

        categorization.setUserId(userId);

        return new CategorizationBuilder(categorization);
    }

    public static CategorizationBuilder withCategory(long categoryId) {
        var categorization = new Categorization();

        categorization.setCategoryId(categoryId);

        return new CategorizationBuilder(categorization);
    }

    public static CategorizationBuilder onField(ProcessedTransactionField field, CategorizationRule rule, boolean booleanValue) {
        var categorization = new Categorization();

        categorization.setRuleBasedOn(field);
        categorization.setCategorizationRule(rule);
        categorization.setBooleanValue(booleanValue);

        return new CategorizationBuilder(categorization);
    }

    public static CategorizationBuilder onField(ProcessedTransactionField field, CategorizationRule rule, double numericValue) {
        var categorization = new Categorization();

        categorization.setRuleBasedOn(field);
        categorization.setCategorizationRule(rule);
        categorization.setNumericValue(numericValue);

        return new CategorizationBuilder(categorization);
    }

    public static CategorizationBuilder onField(ProcessedTransactionField field, CategorizationRule rule, String stringValue) {
        var categorization = new Categorization();

        categorization.setRuleBasedOn(field);
        categorization.setCategorizationRule(rule);
        categorization.setStringValue(stringValue);

        return new CategorizationBuilder(categorization);
    }

    public static CategorizationBuilder onBetween(ProcessedTransactionField field, double greaterThan, double lessThan) {
        var categorization = new Categorization();

        categorization.setRuleBasedOn(field);
        categorization.setCategorizationRule(CategorizationRule.NUMERIC_BETWEEN);
        categorization.setNumericGreaterThan(greaterThan);
        categorization.setNumericLessThan(lessThan);

        return new CategorizationBuilder(categorization);
    }

    public static CategorizationBuilder onBetween(ProcessedTransactionField field, LocalDateTime greaterThan, LocalDateTime lessThan) {
        var categorization = new Categorization();

        categorization.setRuleBasedOn(field);
        categorization.setCategorizationRule(CategorizationRule.TIMESTAMP_BETWEEN);
        categorization.setTimestampGreaterThan(greaterThan);
        categorization.setTimestampLessThan(lessThan);

        return new CategorizationBuilder(categorization);
    }

    public static CategorizationBuilder and(Categorization left, Categorization right) {
        var categorization = new Categorization();

        categorization.setCategorizationRule(CategorizationRule.AND);
        categorization.setLeftCategorizationId(left.getId());
        categorization.setRightCategorizationId(right.getId());

        return new CategorizationBuilder(categorization);
    }

    public static CategorizationBuilder or(Categorization left, Categorization right) {
        var categorization = new Categorization();

        categorization.setCategorizationRule(CategorizationRule.OR);
        categorization.setLeftCategorizationId(left.getId());
        categorization.setRightCategorizationId(right.getId());

        return new CategorizationBuilder(categorization);
    }

    public static CategorizationBuilder not(Categorization right) {
        var categorization = new Categorization();

        categorization.setCategorizationRule(CategorizationRule.NOT);
        categorization.setRightCategorizationId(right.getId());

        return new CategorizationBuilder(categorization);
    }

    public CategorizationBuilder user(int userId) {
        categorization.setUserId(userId);

        return this;
    }

    public CategorizationBuilder category(long categoryId) {
        categorization.setCategoryId(categoryId);

        return this;
    }

    public CategorizationBuilder on(ProcessedTransactionField field, CategorizationRule rule, boolean booleanValue) {
        categorization.setRuleBasedOn(field);
        categorization.setCategorizationRule(rule);
        categorization.setBooleanValue(booleanValue);

        return this;
    }

    public CategorizationBuilder on(ProcessedTransactionField field, CategorizationRule rule, double numericValue) {
        categorization.setRuleBasedOn(field);
        categorization.setCategorizationRule(rule);
        categorization.setNumericValue(numericValue);

        return this;
    }

    public CategorizationBuilder on(ProcessedTransactionField field, CategorizationRule rule, String stringValue) {
        categorization.setRuleBasedOn(field);
        categorization.setCategorizationRule(rule);
        categorization.setStringValue(stringValue);

        return this;
    }

    public CategorizationBuilder between(ProcessedTransactionField field, double greaterThan, double lessThan) {
        categorization.setRuleBasedOn(field);
        categorization.setCategorizationRule(CategorizationRule.NUMERIC_BETWEEN);
        categorization.setNumericGreaterThan(greaterThan);
        categorization.setNumericLessThan(lessThan);

        return this;
    }

    public CategorizationBuilder between(ProcessedTransactionField field, LocalDateTime greaterThan, LocalDateTime lessThan) {
        categorization.setRuleBasedOn(field);
        categorization.setCategorizationRule(CategorizationRule.TIMESTAMP_BETWEEN);
        categorization.setTimestampGreaterThan(greaterThan);
        categorization.setTimestampLessThan(lessThan);

        return this;
    }

    public Categorization build(boolean isRoot) {
        categorization.setId(RandomUtils.nextLong());
        categorization.setRoot(isRoot);

        return categorization;
    }

    public Categorization build() {
        categorization.setId(RandomUtils.nextLong());
        categorization.setRoot(false);

        return categorization;
    }
}
