create table member (
    member_id bigint not null,
    nickname varchar(255),
    password varchar(255) not null,
    username varchar(255) not null,
    primary key (member_id)
);

create table member_roles (
    member_member_id bigint not null,
    roles varchar(255)
);

create table member_seq (
    next_val bigint
);

insert into member_seq values ( 1 );

alter table member_roles
    add constraint FKruptm2dtwl95mfks4bnhv828k
    foreign key (member_member_id)
    references member (member_id);

insert into member_roles (member_member_id, roles) values (1, "USER");