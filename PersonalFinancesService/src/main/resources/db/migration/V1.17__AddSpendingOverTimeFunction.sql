CREATE OR REPLACE FUNCTION statistics.spending_over_time(
    category_ids BIGINT[],
    time_period TEXT,
    from_date TIMESTAMP,
    to_date TIMESTAMP
)
RETURNS TABLE (
                  category_id BIGINT,
                  amount_for_period FLOAT,
                  period_beginning_timestamp TIMESTAMP
              )
LANGUAGE plpgsql
AS $$
DECLARE
    time_interval interval;
BEGIN
    time_interval := CASE
        WHEN time_period = 'DAILY' THEN interval '1 day'
        WHEN time_period = 'WEEKLY' THEN interval '1 week'
        WHEN time_period = 'BIWEEKLY' THEN interval '2 weeks'
        WHEN time_period = 'MONTHLY' THEN interval '1 month'
        WHEN time_period = 'QUARTERLY' THEN interval '3 months'
        WHEN time_period = 'YEARLY' THEN interval '1 year'
        ELSE interval '1 day'
    END;

    RETURN QUERY WITH start_end AS (
        SELECT
            c.id as category_id,
            generate_series(
                    from_date,
                    to_date,
                    time_interval
            ) as period_beginning_timestamp,
            generate_series(
                    from_date,
                    to_date,
                    time_interval
            ) + time_interval AS period_ending_timestamp
        FROM categories.transaction_category AS c
        WHERE c.id = any(category_ids)
    ),
    amounts AS (
        SELECT
            ptc.category_id,
            SUM(pt.amount) AS amount_for_period,
            start_end.period_beginning_timestamp
        FROM start_end
        JOIN categories.processed_transaction_category AS ptc ON ptc.category_id = start_end.category_id
        JOIN transactions.processed_transaction AS pt ON ptc.processed_transaction_id = pt.id
        WHERE
            (
                start_end.period_ending_timestamp > to_date
                AND pt.timestamp BETWEEN start_end.period_beginning_timestamp AND to_date
            )
            OR (
                start_end.period_ending_timestamp <= to_date
                AND pt.timestamp BETWEEN start_end.period_beginning_timestamp AND start_end.period_ending_timestamp
            )
        GROUP BY (
                  start_end.period_beginning_timestamp,
                  start_end.period_ending_timestamp,
                  ptc.category_id
        )
    )
    SELECT
        start_end.category_id,
        COALESCE(amounts.amount_for_period, 0) AS amount_for_period,
        start_end.period_beginning_timestamp
    FROM start_end
    LEFT OUTER JOIN amounts ON amounts.category_id = start_end.category_id AND amounts.period_beginning_timestamp = start_end.period_beginning_timestamp;
END
$$;