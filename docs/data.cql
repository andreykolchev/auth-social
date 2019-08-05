create table if not exists crossmarket.interaction_data
(
    operation_id    uuid,
    command_name    text,
    command_id      text,
    command_date    timestamp,
    request_data    text,
    request_date    timestamp,
    request_status  text,
    response_data   text,
    response_date   timestamp,
    response_status text,
    process_id      text,
    context         text,
    primary key (operation_id, command_name, command_id)
) with clustering order by (command_name asc, command_date desc);

create index if not exists request_status_index
    on crossmarket.interaction_data (request_status);

create index if not exists response_status_index
    on crossmarket.interaction_data (response_status);


create table if not exists crossmarket.interaction_offset
(
    command_name text,
    command_date timestamp,
    offset       text,
    operation_id uuid,
    primary key (command_name, command_date)
) with clustering order by (command_date desc);


create table if not exists crossmarket.integration_data
(
    ext_id    text,
    int_id    text,
    json_data text,
    primary key (ext_id)
);