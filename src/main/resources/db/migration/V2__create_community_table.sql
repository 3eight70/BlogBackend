CREATE TABLE IF NOT EXISTS communities (
                                           id UUID PRIMARY KEY,
                                           create_time TIMESTAMP NOT NULL,
                                           name VARCHAR(255) NOT NULL,
                                           description VARCHAR(255),
                                           is_closed BOOLEAN NOT NULL DEFAULT false,
                                           subscribers_count INT NOT NULL DEFAULT 0,
                                           administrators_count INT NOT NULL DEFAULT 0
);
