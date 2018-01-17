drop table if exists coupon_release_record;

/*==============================================================*/
/* Table: coupon_release_record                                 */
/*==============================================================*/
create table coupon_release_record
(
   id                   int not null auto_increment,
   create_time          datetime,
   from_user            bigint(20),
   to_user              bigint(20),
   coupon_id            varchar(50),
   coupon_release_item_id int,
   primary key (id)
);

/*==============================================================*/
/* Table: coupon_release_record_sales                           */
/*==============================================================*/
create table coupon_release_record_sales
(
   id                   int not null,
   product_id           varchar(50),
   product_name         varchar(100),
   primary key (id)
);

alter table coupon_release_record_sales add constraint FK_fk_cou_rel_rec_sal_ext_cou_rel_rec foreign key (id)
      references coupon_release_record (id) on delete restrict on update restrict;