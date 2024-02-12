CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY,
                                     create_time TIMESTAMP NOT NULL,
                                     full_name VARCHAR(255) NOT NULL,
                                     birth_date TIMESTAMP,
                                     gender VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) UNIQUE NOT NULL,
                                     phone_number VARCHAR(255),
                                     password VARCHAR(1000) NOT NULL
);
