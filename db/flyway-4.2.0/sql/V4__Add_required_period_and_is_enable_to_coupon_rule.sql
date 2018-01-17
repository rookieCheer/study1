alter table coupon_release_rule_item_red add column required_period int null;
alter table coupon_release_rule_item_red add column expire_day int null;

alter table coupon_release_rule_item_more_money add column required_period int null;
alter table coupon_release_rule_item_more_money add column expire_day int null;

alter table coupon_release_rule_item_more_rate add column required_period int null;
alter table coupon_release_rule_item_more_rate add column expire_day int null;

alter table coupon_release_rule add column is_enable bool null;