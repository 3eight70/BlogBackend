CREATE TABLE IF NOT EXISTS full_posts (
                                          id UUID PRIMARY KEY,
                                          create_time TIMESTAMP NOT NULL,
                                          title VARCHAR(255) NOT NULL,
                                          description TEXT NOT NULL,
                                          reading_time INT NOT NULL,
                                          image VARCHAR(255),
                                          author_id UUID NOT NULL,
                                          author VARCHAR(255) NOT NULL,
                                          community_id UUID,
                                          community_name VARCHAR(255),
                                          address_id UUID,
                                          likes INT NOT NULL DEFAULT 0,
                                          has_like BOOLEAN NOT NULL DEFAULT false,
                                          comments_count INT NOT NULL DEFAULT 0
);
