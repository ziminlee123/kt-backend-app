-- 축제 기본 정보
CREATE TABLE IF NOT EXISTS festivals (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  location VARCHAR(300) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  target VARCHAR(100),
  description TEXT,
  status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
  actual_attendees INTEGER,
  satisfaction_score INTEGER,
  total_revenue BIGINT,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- 축제 구역
CREATE TABLE IF NOT EXISTS zones (
  id BIGSERIAL PRIMARY KEY,
  festival_id BIGINT NOT NULL REFERENCES festivals(id) ON DELETE CASCADE,
  name VARCHAR(200) NOT NULL,
  type VARCHAR(20) NOT NULL,
  capacity INTEGER NOT NULL,
  coordinates VARCHAR(100),
  notes TEXT,
  current_capacity INTEGER,
  congestion_level INTEGER,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- 혼잡도(시간 단위)
CREATE TABLE IF NOT EXISTS crowd_metrics (
  id BIGSERIAL PRIMARY KEY,
  zone_id BIGINT NOT NULL REFERENCES zones(id) ON DELETE CASCADE,
  ts TIMESTAMP NOT NULL,
  level SMALLINT NOT NULL CHECK (level BETWEEN 1 AND 3),
  headcount INTEGER,
  UNIQUE(zone_id, ts)
);

-- SNS 피드백
CREATE TABLE IF NOT EXISTS sns_feedback (
  id BIGSERIAL PRIMARY KEY,
  festival_id BIGINT NOT NULL REFERENCES festivals(id) ON DELETE CASCADE,
  issue VARCHAR(300) NOT NULL,
  mentions INTEGER NOT NULL,
  sentiment VARCHAR(20) NOT NULL,
  platform VARCHAR(50),
  severity_level INTEGER,
  is_resolved BOOLEAN DEFAULT FALSE,
  resolution TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  resolved_at TIMESTAMP
);

-- SNS 일별 데이터
CREATE TABLE IF NOT EXISTS sns_daily (
  id BIGSERIAL PRIMARY KEY,
  festival_id BIGINT NOT NULL REFERENCES festivals(id) ON DELETE CASCADE,
  date DATE NOT NULL,
  positive INTEGER DEFAULT 0,
  negative INTEGER DEFAULT 0,
  neutral INTEGER DEFAULT 0,
  tips TEXT
);

-- 매출 일별
CREATE TABLE IF NOT EXISTS sales_daily (
  id BIGSERIAL PRIMARY KEY,
  festival_id BIGINT NOT NULL REFERENCES festivals(id) ON DELETE CASCADE,
  date DATE NOT NULL,
  amount BIGINT DEFAULT 0
);

-- 방문자 KPI
CREATE TABLE IF NOT EXISTS visitor_kpi (
  id BIGSERIAL PRIMARY KEY,
  festival_id BIGINT NOT NULL REFERENCES festivals(id) ON DELETE CASCADE,
  date DATE NOT NULL,
  visitors INTEGER DEFAULT 0
);
