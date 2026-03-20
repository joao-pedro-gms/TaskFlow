CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name  VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE categories (
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name      VARCHAR(100) NOT NULL,
    color_hex VARCHAR(7) DEFAULT '#6366f1',
    icon_name VARCHAR(50) DEFAULT 'tag',
    UNIQUE(user_id, name)
);

CREATE TABLE tasks (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id   BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    title         VARCHAR(500) NOT NULL,
    description   TEXT,
    deadline      TIMESTAMP,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                  CHECK (status IN ('PENDING','IN_PROGRESS','COMPLETED')),
    priority      VARCHAR(10) NOT NULL DEFAULT 'MEDIUM'
                  CHECK (priority IN ('LOW','MEDIUM','HIGH')),
    reminder_sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at  TIMESTAMP
);

CREATE TABLE daily_priorities (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    task_id       BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    priority_date DATE NOT NULL,
    rank          INT NOT NULL CHECK (rank BETWEEN 1 AND 3),
    completed     BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(user_id, priority_date, rank),
    UNIQUE(user_id, priority_date, task_id)
);

CREATE INDEX idx_tasks_user_status ON tasks(user_id, status);
CREATE INDEX idx_tasks_deadline ON tasks(deadline) WHERE status != 'COMPLETED';
CREATE INDEX idx_daily_priorities_user_date ON daily_priorities(user_id, priority_date);
