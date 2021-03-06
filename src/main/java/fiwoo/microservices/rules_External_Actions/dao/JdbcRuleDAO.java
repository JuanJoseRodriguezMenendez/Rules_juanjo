package fiwoo.microservices.rules_External_Actions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import fiwoo.microservices.rules_External_Actions.model.RuleDB;


public class JdbcRuleDAO implements RuleDBDAO{
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public String insert(RuleDB rule) {
		String sql = "INSERT INTO perseo_rules" +
				"(RULE_ID, USER_ID, RULE_NAME, RULE_DESCRIPTION, RULE, ORION_ID) "
				+ "VALUES (?,?,?,?,?,?)";
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, rule.getRule_id());
			ps.setString(2, rule.getUser_id());
			ps.setString(3, rule.getRule_name());
			ps.setString(4, rule.getRule_description());
			ps.setString(5, rule.getRule());
			ps.setString(6, rule.getOrion_id());
			ps.executeUpdate();
			ps.close();
			return "{\"201\":\"created\"}";
		} catch (SQLException e) {
			return "{\""+ e.getErrorCode() + "\":" + "\"" +e.getMessage()+ "\"}";
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public List<RuleDB> findByUser(String user_id) {

		String sql = "SELECT * FROM perseo_rules WHERE USER_ID = ?";

		Connection conn = null;
		List<RuleDB> rules = new ArrayList<RuleDB>();
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, user_id);
			RuleDB rule = null;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rule = new RuleDB(
					rs.getString("RULE_ID"),
					rs.getString("USER_ID"),
					rs.getString("RULE_NAME"),
					rs.getString("RULE_DESCRIPTION"),
					rs.getString("RULE"),
					rs.getString("ORION_ID")
				);
				rules.add(rule);
			}
			rs.close();
			ps.close();
			return rules;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException e) {}
			}
		}
	}

	@Override
	public int delete(String rule_id, String user_id) {
		String sql = "DELETE FROM perseo_rules WHERE USER_ID = ? and RULE_ID = ?";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, user_id);
			ps.setString(2, rule_id);
			int rs = ps.executeUpdate();
			ps.close();
			return rs;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException e) {}
			}
		}
		
	}

	@Override
	public boolean existsRule(String rule_id, String user_id) {
		String sql = "SELECT * FROM perseo_rules WHERE RULE_ID = ? and USER_ID = ?";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, rule_id);
			ps.setString(2, user_id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.close();
				ps.close();
				return true;
			}
			rs.close();
			ps.close();
			return false;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException e) {}
			}
		}
	}

	@Override
	public String getSubscriptionId(String rule_id) {
			String sql = "SELECT ORION_ID FROM perseo_rules WHERE RULE_ID = ?";

			Connection conn = null;

			try {
				conn = dataSource.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, rule_id);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					String id = rs.getString("ORION_ID");
					rs.close();
					ps.close();
					return id;
				}
				rs.close();
				ps.close();
				return "";
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				if (conn != null) {
					try {
					conn.close();
					} catch (SQLException e) {}
				}
			}
	}
	
	@Override
	public void createTable() {
		Connection conn = null;
	    String sqlCreate = "CREATE TABLE IF NOT EXISTS perseo_rules"
	            + "( RULE_ID varchar(100) NOT NULL,"
	            + " USER_ID varchar(100) NOT NULL,"
	            + " RULE_NAME varchar(100),"
	            + " RULE_DESCRIPTION varchar(100),"
	            + " RULE varchar(5000) NOT NULL,"
	            + " ORION_ID varchar(100) NOT NULL,"
	            + " PRIMARY KEY (RULE_ID))";
	    
		try {
			conn = dataSource.getConnection();
		    Statement stmt;
			stmt = conn.createStatement();
		    stmt.execute(sqlCreate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
