-- 1. Создать таблицу для блокировок платформ
CREATE TABLE IF NOT EXISTS platform_locks (
    player_uuid UUID PRIMARY KEY,
    platform VARCHAR(20) NOT NULL,
    locked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Индекс для быстрой очистки старых блокировок
CREATE INDEX IF NOT EXISTS idx_platform_locks_locked_at ON platform_locks(locked_at);

-- 2. Функция: проверка и блокировка платформы
CREATE OR REPLACE FUNCTION try_start_clicking(
    p_player_uuid UUID,
    p_platform VARCHAR
) RETURNS TABLE(can_click BOOLEAN, blocked_by VARCHAR) AS $$
DECLARE
    v_existing_platform VARCHAR;
    v_locked_at TIMESTAMP;
BEGIN
    SELECT platform, locked_at
    INTO v_existing_platform, v_locked_at
    FROM platform_locks
    WHERE player_uuid = p_player_uuid;

    IF v_existing_platform IS NULL THEN
        INSERT INTO platform_locks (player_uuid, platform, locked_at)
        VALUES (p_player_uuid, p_platform, CURRENT_TIMESTAMP)
        ON CONFLICT (player_uuid) DO UPDATE
        SET platform = p_platform, locked_at = CURRENT_TIMESTAMP;

        RETURN QUERY SELECT TRUE, NULL::VARCHAR;
    ELSIF v_existing_platform = p_platform THEN
        UPDATE platform_locks
        SET locked_at = CURRENT_TIMESTAMP
        WHERE player_uuid = p_player_uuid;

        RETURN QUERY SELECT TRUE, NULL::VARCHAR;
    ELSE
        RETURN QUERY SELECT FALSE, v_existing_platform;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- 3. Функция: разблокировка игрока
CREATE OR REPLACE FUNCTION unlock_platform(p_player_uuid UUID) RETURNS VOID AS $$
BEGIN
    DELETE FROM platform_locks WHERE player_uuid = p_player_uuid;
END;
$$ LANGUAGE plpgsql;

-- 4. Функция: очистка старых блокировок (>5 минут)
CREATE OR REPLACE FUNCTION cleanup_old_locks() RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM platform_locks
    WHERE locked_at < NOW() - INTERVAL '5 minutes';

    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;