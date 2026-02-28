-- File: backend-timebox/src/main/resources/db/migration/V1__init_schema.sql
-- Timebox Planner 초기 스키마

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token TEXT NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name VARCHAR(30) NOT NULL,
    color VARCHAR(7),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, name)
);

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' CHECK (
        priority IN ('HIGH', 'MEDIUM', 'LOW')
    ),
    estimated_minutes INTEGER CHECK (
        estimated_minutes > 0
        AND estimated_minutes % 5 = 0
    ),
    status VARCHAR(20) NOT NULL DEFAULT 'TODO' CHECK (
        status IN (
            'TODO',
            'IN_PROGRESS',
            'DONE',
            'CANCELLED'
        )
    ),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE task_tags (
    task_id BIGINT NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tags (id) ON DELETE CASCADE,
    PRIMARY KEY (task_id, tag_id)
);

CREATE TABLE timeboxes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    task_id BIGINT REFERENCES tasks (id) ON DELETE SET NULL,
    date DATE NOT NULL,
    start_time VARCHAR(5) NOT NULL,
    end_time VARCHAR(5) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED' CHECK (
        status IN (
            'PLANNED',
            'RUNNING',
            'PAUSED',
            'COMPLETED',
            'CANCELLED'
        )
    ),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, date, start_time)
);

CREATE TABLE focus_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    timebox_id BIGINT NOT NULL REFERENCES timeboxes (id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'RUNNING' CHECK (
        status IN (
            'RUNNING',
            'PAUSED',
            'COMPLETED',
            'CANCELLED'
        )
    ),
    planned_minutes INTEGER NOT NULL,
    focused_minutes INTEGER,
    total_paused_minutes INTEGER NOT NULL DEFAULT 0,
    started_at TIMESTAMP NOT NULL,
    paused_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE retrospectives (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL UNIQUE REFERENCES focus_sessions (id) ON DELETE CASCADE,
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    memo TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 인덱스
CREATE INDEX idx_tasks_user_status ON tasks (user_id, status);

CREATE INDEX idx_tasks_user_priority ON tasks (user_id, priority);

CREATE INDEX idx_timeboxes_user_date ON timeboxes (user_id, date);

CREATE INDEX idx_sessions_user_status ON focus_sessions (user_id, status);

CREATE INDEX idx_sessions_timebox ON focus_sessions (timebox_id);

CREATE INDEX idx_tags_user_id ON tags (user_id);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id);