CREATE TABLE IF NOT EXISTS comments (
                                        id UUID PRIMARY KEY,
                                        create_time TIMESTAMP NOT NULL,
                                        content VARCHAR(255) NOT NULL,
                                        modified_date TIMESTAMP,
                                        delete_date TIMESTAMP,
                                        author_id UUID NOT NULL,
                                        author VARCHAR(255) NOT NULL,
                                        sub_comments INT NOT NULL,
                                        parent_id UUID
);

ALTER TABLE comments
    ADD CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES users(id);

ALTER TABLE comments
    ADD CONSTRAINT fk_parent_id FOREIGN KEY (parent_id) REFERENCES comments(id);
