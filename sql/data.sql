INSERT INTO festivals (name, location, start_date, end_date, target, description, status, actual_attendees, satisfaction_score, total_revenue)
VALUES ('빛축제 2025', '부산 해운대 해변공원', '2025-12-01', '2025-12-10', '50000명', '부산 해운대에서 열리는 겨울 빛축제', 'DURING', 45000, 85, 2800000000);

INSERT INTO zones (festival_id, name, type, capacity, coordinates, notes, current_capacity, congestion_level)
VALUES 
(1, '메인광장', 'MAIN_STAGE', 800, '35.1590, 129.1608', '주무대 및 메인 이벤트 공간', 680, 85),
(1, '푸드존', 'FOOD_COURT', 300, '35.1580, 129.1615', '다양한 음식 부스와 휴식 공간', 180, 60);

INSERT INTO crowd_metrics (zone_id, ts, level, headcount)
VALUES (1, '2025-12-01 18:00:00', 2, 600);

-- SNS 피드백 데이터
INSERT INTO sns_feedback (festival_id, issue, mentions, sentiment, platform, severity_level, is_resolved, resolution)
VALUES 
(1, '메인광장 입구 대기줄이 너무 길어요', 45, 'NEGATIVE', 'Instagram', 4, TRUE, '추가 입구 개방 및 대기줄 정리 요원 배치'),
(1, '푸드존 음식이 정말 맛있어요!', 82, 'POSITIVE', 'Twitter', 1, FALSE, NULL),
(1, '화장실이 부족한 것 같아요', 23, 'NEGATIVE', 'Facebook', 3, FALSE, NULL);

INSERT INTO sns_daily (festival_id, date, positive, negative, neutral, tips)
VALUES (1, '2025-12-01', 120, 50, 30, '메인광장 동선 보강');

INSERT INTO sales_daily (festival_id, date, amount)
VALUES (1, '2025-12-01', 78000000);

INSERT INTO visitor_kpi (festival_id, date, visitors)
VALUES (1, '2025-12-01', 18000);

-- 추가 축제 데이터
INSERT INTO festivals (name, location, start_date, end_date, target, description, status)
VALUES 
('서울 벚꽃축제', '여의도 한강공원', '2025-04-05', '2025-04-15', '100000명', '여의도에서 펼쳐지는 벚꽃 축제', 'BEFORE'),
('제주 감귤축제', '제주시 중앙공원', '2025-11-20', '2025-11-25', '30000명', '제주 특산 감귤을 테마로 한 축제', 'BEFORE');

-- 추가 구역 데이터
INSERT INTO zones (festival_id, name, type, capacity, coordinates, notes, current_capacity, congestion_level)
VALUES 
(1, 'VIP 라운지', 'VIP', 100, '35.1585, 129.1612', '프리미엄 고객 전용 공간', 45, 45),
(2, '벚꽃 산책로', 'MAIN_STAGE', 5000, '37.5219, 126.9308', '벚꽃나무 아래 메인 무대', 0, 0),
(2, '피크닉존', 'FOOD_COURT', 2000, '37.5215, 126.9315', '가족 단위 피크닉 공간', 0, 0),
(3, '감귤 체험장', 'MERCHANDISE', 500, '33.4996, 126.5312', '감귤 따기 체험 공간', 0, 0);

