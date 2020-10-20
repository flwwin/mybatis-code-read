# 创建user表DDL
drop table users;
CREATE TABLE IF NOT EXISTS `users`
(
    `id`           INT UNSIGNED AUTO_INCREMENT comment '主键',
    `name`         VARCHAR(100) NOT NULL comment '名字',
    `age`          VARCHAR(40)  NOT NULL comment '年龄',
    `sex`          VARCHAR(40) comment '性别(1-男 2-女 3-不明)',
    `email`        VARCHAR(40) comment '邮箱',
    `phone_number` VARCHAR(40) comment '电话号码',
    `labels`       VARCHAR(40) comment '标签',
    `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    `update_time`  datetime not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = INNODB comment ='用户表'
  DEFAULT CHARSET = GBK;

insert into users (name, age, sex, email, phone_number, labels)
values ('小米', '10', '1', 'xxx.@qq.com', '1379454', '456'),
       ('小米', '10', '1', 'xxx.@qq.com', '1379454', '456'),
       ('小菜', '20', '1', 'xxx.@qq.com', '1379454', '456');

update users set name = '小丽', phone_number = '789456', sex='2' where id=2;

select CURRENT_TIMESTAMP from dual;