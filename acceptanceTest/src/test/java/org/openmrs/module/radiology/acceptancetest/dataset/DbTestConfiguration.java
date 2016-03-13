package org.openmrs.module.radiology.acceptancetest.dataset;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javax.sql.DataSource;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.*;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.junit.After;
import org.junit.Before;
import org.xml.sax.InputSource;

public class DbTestConfiguration {
  private DataSource dataSource;
  private IDatabaseConnection databaseConnection;
  public DbTestSelectSupport defaultSupport;

  private static final String JDBC_DRIVER = System.getProperty("db.driver");
  private static final String JDBC_URL = System.getProperty("db.url");
  private static final String USER = System.getProperty("db.userid");
  private static final String PASSWORD = System.getProperty("db.password");

  @Before
  public void setupConnections() throws SQLException, DatabaseUnitException, ClassNotFoundException {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setURL(System.getProperty("db.url"));
    dataSource.setUser(System.getProperty("db.userid"));
    dataSource.setPassword(System.getProperty("db.password"));
    this.dataSource = dataSource;

    DatabaseConnection databaseConnection = new DatabaseConnection(getDataSource().getConnection());
    databaseConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
    this.databaseConnection = databaseConnection;

    defaultSupport = new DbTestSelectSupport(getDataSource());
  }

  @After
  public void shutdownConnections() throws SQLException {
    if (databaseConnection != null) {
      databaseConnection.close();
    }
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public IDatabaseConnection getDatabaseConnection() {
    return databaseConnection;
  }
  
  public FlatXmlDataSet getDataSet(String fileName) throws DataSetException {
    return new FlatXmlDataSet(new FlatXmlProducer(new InputSource(getClass().getResourceAsStream(fileName))));
  }
}
