package com.huoq.common;

import org.hibernate.Hibernate;
import org.hibernate.dialect.MySQLInnoDBDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;

import java.sql.Types;

public class MysqlDailetExtend extends MySQLInnoDBDialect {
	public  MysqlDailetExtend() {  
        super ();  
        registerFunction("date_add_interval", new SQLFunctionTemplate(Hibernate.DATE, "date_add(?1, INTERVAL ?2 ?3)"));
        registerHibernateType(Types.LONGVARCHAR, Hibernate.TEXT.getName());
    }  
}
