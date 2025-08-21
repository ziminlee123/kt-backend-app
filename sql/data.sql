INSERT INTO festivals (name, region, start_date, end_date, target, is_open)
VALUES ('빛축제 2025', '부산 해운대', '2025-12-01', '2025-12-10', '가족/연인', TRUE);

INSERT INTO zones (festival_id, name, capacity)
VALUES (1, '메인광장', 800), (1, '푸드존', 300);

INSERT INTO crowd_metrics (zone_id, ts, level, headcount)
VALUES (1, '2025-12-01 18:00:00', 2, 600);

INSERT INTO sns_daily (festival_id, date, positive, negative, neutral, tips)
VALUES (1, '2025-12-01', 120, 50, 30, '메인광장 동선 보강');

INSERT INTO sales_daily (festival_id, date, amount)
VALUES (1, '2025-12-01', 78000000);

INSERT INTO visitor_kpi (festival_id, date, visitors)
VALUES (1, '2025-12-01', 18000);

