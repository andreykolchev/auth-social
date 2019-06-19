CREATE KEYSPACE IF NOT EXISTS crossmarket WITH replication = {'class':'SimpleStrategy', 'replication_factor':2};

CREATE TABLE IF NOT EXISTS crossmarket.auth_user
(
    provider_id    text not null,
    person_id      text,
    provider       text,
    status         text,
    name           text,
    email          text,
    hashedPassword text,
    primary key (provider_id)
);

CREATE MATERIALIZED VIEW crossmarket.auth_user_view
AS
SELECT email, provider_id
FROM crossmarket.auth_user
WHERE provider_id IS NOT NULL AND person_id IS NOT NULL AND email IS NOT NULL
PRIMARY KEY ((email), provider_id)

INSERT INTO auth_user (person_id,provider,provider_id,email,status,name,hashedPassword) VALUES (1eae43dc-2c4d-4970-aa00-ccf1ddf7bee0,'facebook','2265596506834324','andreykolchev@gmail.com','created','Andrey Kolchev',null);

CREATE TABLE IF NOT EXISTS crossmarket.auth_history
(
    operation_id   uuid,
    command        text,
    operation_date timestamp,
    input_data     text,
    output_data    text,
    primary key (operation_id, command)
);