CREATE KEYSPACE IF NOT EXISTS crossmarket WITH replication = {'class':'SimpleStrategy', 'replication_factor':2};

CREATE TABLE IF NOT EXISTS crossmarket.auth_user
(
    provider_id    text,
    person_id      text,
    provider       text,
    status         text,
    name           text,
    email          text,
    hashedPassword text,
    primary key (provider_id)
);

CREATE MATERIALIZED VIEW crossmarket.auth_user_by_person_id
AS
SELECT provider_id, person_id
FROM crossmarket.auth_user
WHERE provider_id IS NOT NULL
  AND person_id IS NOT NULL
    PRIMARY KEY ((person_id), provider_id)
    WITH caching = { 'keys' : 'ALL', 'rows_per_partition' : '100' }
    AND comment = 'Based on table auth_user';

INSERT INTO auth_user (person_id,provider,provider_id,email,status,name,hashedPassword) VALUES (1eae43dc-2c4d-4970-aa00-ccf1ddf7bee0,'facebook','2265596506834324','andreykolchev@gmail.com','created','Andrey Kolchev',null);

CREATE TABLE IF NOT EXISTS crossmarket.auth_history
(
    operation_id UUID,
    command      text,
    input_ata    text,
    output_ata   text,
    primary key (operation_id, command)
);