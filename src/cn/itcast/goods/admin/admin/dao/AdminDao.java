package cn.itcast.goods.admin.admin.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import cn.itcast.goods.admin.admin.domain.Admin;
import cn.itcast.jdbc.TxQueryRunner;

public class AdminDao {
	QueryRunner qr = new TxQueryRunner();
	
	/*
	 * *通过管理员名和密码查询
	 */
	public Admin find(String adminname, String adminpwd) throws SQLException {
		String sql = "select * from t_admin where adminname=? and adminpwd=?";
		return qr.query(sql, new BeanHandler<Admin>(Admin.class), adminname, adminpwd);
	}
}
