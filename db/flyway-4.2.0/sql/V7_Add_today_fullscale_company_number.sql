ALTER TABLE platform ADD today_fullscale_company_number INT (5) COMMENT '今日满标企业数量';
ALTER TABLE platform ADD all_buy_money DOUBLE DEFAULT 0 COMMENT '平台总交易额(元)';
ALTER TABLE platform ADD unchecked_out_cash_money DOUBLE DEFAULT 0 COMMENT '未审核提现总额(元)';