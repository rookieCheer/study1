/*==============================================================*/
/* Table: coupon_release_record                                 */
/*==============================================================*/
create table coupon_release_record
(
   id                   int not null,
   primary key (id)
);

/*==============================================================*/
/* Table: coupon_release_rule                                   */
/*==============================================================*/
create table coupon_release_rule
(
   id                   int not null auto_increment,
   name                 varchar(255),
   can_delete           bool,
   to_all_product       bool,
   primary key (id)
);

/*==============================================================*/
/* Table: coupon_release_rule_distribution                      */
/*==============================================================*/
create table coupon_release_rule_distribution
(
   id                   int not null,
   primary key (id)
);

/*==============================================================*/
/* Table: coupon_release_rule_invite                            */
/*==============================================================*/
create table coupon_release_rule_invite
(
   id                   int not null,
   primary key (id)
);

/*==============================================================*/
/* Table: coupon_release_rule_item                              */
/*==============================================================*/
create table coupon_release_rule_item
(
   id                   int not null auto_increment,
   coupon_release_rule_id int,
   primary key (id)
);

/*==============================================================*/
/* Table: coupon_release_rule_item_cash                         */
/*==============================================================*/
create table coupon_release_rule_item_cash
(
   id                   int not null,
   value                double,
   value_type           int comment '1.固定数值
            2.根据产品额度计算，如果是此类型，则使用条件的数值为1-100的百分比数值',
   primary key (id)
);

/*==============================================================*/
/* Table: coupon_release_rule_item_more_money                   */
/*==============================================================*/
create table coupon_release_rule_item_more_money
(
   id                   int not null,
   value_type           int comment '1.固定数值
            2.根据产品额度计算，如果是此类型，则使用条件的数值为1-100的百分比数值',
   value                double,
   condition_type       int comment '1.固定数值
            2.根据产品额度计算，如果是此类型，则使用条件的数值为1-100的百分比数值',
   condition_value      double,
   primary key (id)
);

/*==============================================================*/
/* Table: coupon_release_rule_item_more_rate                    */
/*==============================================================*/
create table coupon_release_rule_item_more_rate
(
   id                   int not null,
   value_type           int comment '1.固定数值
            2.根据产品额度计算，如果是此类型，则使用条件的数值为1-100的百分比数值',
   value                double,
   condition_type       int comment '1.固定数值
            2.根据产品额度计算，如果是此类型，则使用条件的数值为1-100的百分比数值',
   condition_value      double,
   primary key (id)
);

/*==============================================================*/
/* Table: coupon_release_rule_item_red                          */
/*==============================================================*/
create table coupon_release_rule_item_red
(
   id                   int not null,
   value_type           int comment '1.固定数值
            2.根据产品额度计算，如果是此类型，则使用条件的数值为1-100的百分比数值',
   value                double,
   condition_type       int comment '1.固定数值
            2.根据产品额度计算，如果是此类型，则使用条件的数值为1-100的百分比数值',
   condition_value      double,
   primary key (id)
);

/*==============================================================*/
/* Table: coupon_release_rule_sales                             */
/*==============================================================*/
create table coupon_release_rule_sales
(
   id                   int not null,
   primary key (id)
);

/*==============================================================*/
/* Table: coupon_release_rule_to_product_collection             */
/*==============================================================*/
create table coupon_release_rule_to_product_collection
(
   coupon_release_rule_sales_id int,
   product_id           varchar(50)
);

alter table coupon_release_rule_distribution add constraint FK_fk_cou_rel_rul_dis_ext_cou_rel_rul foreign key (id)
      references coupon_release_rule (id) on delete restrict on update restrict;

alter table coupon_release_rule_invite add constraint FK_fk_cou_rel_rul_inv_ext_cou_rel_rul foreign key (id)
      references coupon_release_rule (id) on delete restrict on update restrict;

alter table coupon_release_rule_item add constraint FK_fk_cou_rel_rul_ite_2_cou_rel_rul foreign key (coupon_release_rule_id)
      references coupon_release_rule (id) on delete restrict on update restrict;

alter table coupon_release_rule_item_cash add constraint FK_fk_cas_cou_rel_use_rul_ext_cou_rel_rul_ite foreign key (id)
      references coupon_release_rule_item (id) on delete restrict on update restrict;

alter table coupon_release_rule_item_more_money add constraint FK_fk_mor_mon_cou_rel_use_rul_ext_cou_rel_rul_ite foreign key (id)
      references coupon_release_rule_item (id) on delete restrict on update restrict;

alter table coupon_release_rule_item_more_rate add constraint FK_fk_mor_rat_cou_rel_use_rul_ext_cou_rel_rul_ite foreign key (id)
      references coupon_release_rule_item (id) on delete restrict on update restrict;

alter table coupon_release_rule_item_red add constraint FK_fk_red_cou_rel_use_rul_ext_cou_rel_rul_ite foreign key (id)
      references coupon_release_rule_item (id) on delete restrict on update restrict;

alter table coupon_release_rule_sales add constraint FK_fk_cou_rel_rul_sal_ext_cou_rel_rul foreign key (id)
      references coupon_release_rule (id) on delete restrict on update restrict;

alter table coupon_release_rule_to_product_collection add constraint FK_fk_cou_rel_rul_to_pro_col_2_cou_rel_rul_sal foreign key (coupon_release_rule_sales_id)
      references coupon_release_rule_sales (id) on delete restrict on update restrict;

insert into coupon_release_rule(id, name, can_delete, to_all_product, is_enable) values (1, '邀请好友好友得卷', 0, 1, 1);
insert into coupon_release_rule(id, name, can_delete, to_all_product, is_enable) values (2, '邀请好友自己得卷', 0, 1, 1);
insert into coupon_release_rule(id, name, can_delete, to_all_product, is_enable) values (3, '新用户注册', 0, 1, 1);
insert into coupon_release_rule(id, name, can_delete, to_all_product, is_enable) values (4, '分销自己得卷', 0, 1, 1);
insert into coupon_release_rule_invite(id) values (1);
insert into coupon_release_rule_invite(id) values (2);
insert into coupon_release_rule_invite(id) values (3);
insert into coupon_release_rule_distribution(id) values (4);