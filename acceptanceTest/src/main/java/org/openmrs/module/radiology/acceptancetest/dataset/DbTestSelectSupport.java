/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.acceptancetest.dataset;

import org.springframework.dao.EmptyResultDataAccessException;
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

    public Boolean ifOrderExists(final String id, final String patientID, final String providerID, final String performedStatus) {
        try {
            boolean orderExistsInTable__encounter = getJdbcTemplate().queryForObject("SELECT * FROM encounter WHERE encounter_id = ?", new Object[]{id}, new RowMapper<Boolean>() {
                public Boolean mapRow(ResultSet rs, int i) throws SQLException {
                    if (rs.getString("patient_id").equals(patientID)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            boolean orderExistsInTable__encounter_provider = getJdbcTemplate().queryForObject("SELECT * FROM encounter_provider WHERE encounter_id = ?", new Object[]{id}, new RowMapper<Boolean>() {
                public Boolean mapRow(ResultSet rs, int i) throws SQLException {
                    if (rs.getString("encounter_provider_id").equals(id)) {
                        if (rs.getString("provider_id").equals(providerID)) {
                            return true;
                        }
                    }
                    return false;
                }
            });

            boolean orderExistsInTable__orders = getJdbcTemplate().queryForObject("SELECT * FROM orders WHERE order_id = ?", new Object[]{id}, new RowMapper<Boolean>() {
                public Boolean mapRow(ResultSet rs, int i) throws SQLException {
                    if (rs.getString("encounter_id").equals(id)) {
                        if (rs.getString("patient_id").equals(patientID)) {
                            return true;
                        }
                    }
                    return false;
                }
            });

            boolean orderExistsInTable__test_order = getJdbcTemplate().queryForObject("SELECT * FROM test_order WHERE order_id = ?", new Object[]{id}, new RowMapper<Boolean>() {
                public Boolean mapRow(ResultSet rs, int i) throws SQLException {
                    return true;
                }
            });

            boolean orderExistsInTable__radiology_order = getJdbcTemplate().queryForObject("SELECT * FROM radiology_order WHERE order_id = ?", new Object[]{id}, new RowMapper<Boolean>() {
                public Boolean mapRow(ResultSet rs, int i) throws SQLException {
                    return true;
                }
            });

            boolean orderExistsInTable__radiology_study = getJdbcTemplate().queryForObject("SELECT * FROM radiology_study WHERE study_id = ?", new Object[]{id}, new RowMapper<Boolean>() {
                public Boolean mapRow(ResultSet rs, int i) throws SQLException {
                    if (rs.getString("order_id").equals(id)) {
                        if (rs.getString("performed_status").equals(performedStatus)) {
                            return true;
                        }
                    }
                    return false;
                }
            });

            if (orderExistsInTable__encounter) {
                if (orderExistsInTable__encounter_provider) {
                    if (orderExistsInTable__orders) {
                        if (orderExistsInTable__test_order) {
                            if (orderExistsInTable__radiology_order) {
                                if (orderExistsInTable__radiology_study) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return false;

        }
        catch(EmptyResultDataAccessException ex)
        {
            return false;
        }
    }

}