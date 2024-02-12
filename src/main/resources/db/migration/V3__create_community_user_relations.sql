CREATE TABLE IF NOT EXISTS community_administrators (
                                                        community_id UUID,
                                                        administrator_id UUID,
                                                        FOREIGN KEY (community_id) REFERENCES communities(id),
                                                        FOREIGN KEY (administrator_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS community_subscribers (
                                                     community_id UUID,
                                                     subscriber_id UUID,
                                                     FOREIGN KEY (community_id) REFERENCES communities(id),
                                                     FOREIGN KEY (subscriber_id) REFERENCES users(id)
);
