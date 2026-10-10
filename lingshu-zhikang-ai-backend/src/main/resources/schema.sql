CREATE TABLE IF NOT EXISTS medicine_reminder (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(120) NOT NULL,
  dosage VARCHAR(120),
  frequency VARCHAR(80),
  reminder_times TEXT,
  start_date DATE,
  end_date DATE,
  notes VARCHAR(500),
  is_active TINYINT(1) DEFAULT 1,
  color VARCHAR(30),
  create_time DATETIME,
  update_time DATETIME,
  INDEX idx_medicine_reminder_user (user_id)
);

CREATE TABLE IF NOT EXISTS medicine_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  reminder_id BIGINT NOT NULL,
  record_date DATE NOT NULL,
  reminder_time VARCHAR(16) NOT NULL,
  status VARCHAR(20) NOT NULL,
  create_time DATETIME,
  update_time DATETIME,
  UNIQUE KEY uk_medicine_record_slot (user_id, reminder_id, record_date, reminder_time),
  INDEX idx_medicine_record_user_date (user_id, record_date)
);

CREATE TABLE IF NOT EXISTS health_report_metadata (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  file_id VARCHAR(255) NOT NULL,
  file_name VARCHAR(255),
  title VARCHAR(255),
  content_type VARCHAR(120),
  file_size BIGINT DEFAULT 0,
  created_time DATETIME,
  updated_time DATETIME,
  UNIQUE KEY uk_health_report_file (file_id),
  INDEX idx_health_report_user (user_id)
);
