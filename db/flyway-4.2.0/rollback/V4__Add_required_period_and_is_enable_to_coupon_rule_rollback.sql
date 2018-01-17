alter table coupon_release_rule_item_red drop column required_period;
alter table coupon_release_rule_item_red drop column expire_day;

alter table coupon_release_rule_item_more_money drop column required_period;
alter table coupon_release_rule_item_more_money drop column expire_day;

alter table coupon_release_rule_item_more_rate drop column required_period;
alter table coupon_release_rule_item_more_rate drop column expire_day;

alter table coupon_release_rule drop column is_enable bool;