CREATE TABLE IF NOT EXISTS tags (
                                    id UUID PRIMARY KEY,
                                    create_time TIMESTAMP NOT NULL,
                                    name VARCHAR(255) NOT NULL
);
