-- auto-generated definition
create table codesolve
(
    id          bigint auto_increment
        primary key,
    title       varchar(255)     not null,
    description text             not null,
    create_time bigint           null,
    view_count  bigint default 0 not null,
    like_count  bigint default 0 not null,
    tag         varchar(512)     not null,
    creator     bigint           not null,
    limitation  int    default 0 not null
);

