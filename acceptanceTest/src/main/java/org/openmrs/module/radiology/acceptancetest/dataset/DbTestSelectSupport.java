package org.openmrs.module.radiology.acceptancetest.dataset;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbTestSelectSupport extends JdbcDaoSupport{

    public DbTestSelectSupport(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Boolean patientExists(final String id, final String name) {
        return getJdbcTemplate().queryForObject("SELECT * FROM person_name WHERE person_name_id = ?", new Object[] {id}, new RowMapper<Boolean>() {
            public Boolean mapRow(ResultSet rs, int i) throws SQLException {
                if(rs.getString("given_name").equals(name))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
    }

    public Boolean providerExists(final String id, final String name) {
        return getJdbcTemplate().queryForObject("SELECT * FROM provider WHERE provider_id = ?", new Object[] {id}, new RowMapper<Boolean>() {
            public Boolean mapRow(ResultSet rs, int i) throws SQLException {
                if(rs.getString("name").equals(name))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
    }

}