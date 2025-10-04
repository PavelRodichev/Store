--changeset rodichev:1
ALTER TABLE users
    ALTER COLUMN password TYPE VARCHAR(128),
    ALTER COLUMN password SET DEFAULT '{noop}123'