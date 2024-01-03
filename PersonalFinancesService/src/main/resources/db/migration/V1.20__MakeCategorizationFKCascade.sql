ALTER TABLE categories.categorization DROP CONSTRAINT FK_categorization_category;

ALTER TABLE categories.categorization
ADD CONSTRAINT FK_categorization_category FOREIGN KEY (category_id) REFERENCES categories.transaction_category(id) ON DELETE CASCADE;