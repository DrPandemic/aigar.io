package io.aigar

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._

trait HelperSpec {
  def getDatabase() = {
    val cpds = new ComboPooledDataSource();
    cpds.setDriverClass( "org.h2.Driver" ); //loads the jdbc driver
    cpds.setJdbcUrl( "jdbc:h2:mem:test" );
    cpds.setUser("root");
    cpds.setPassword("");

    // the settings below are optional -- c3p0 can work with defaults
    cpds.setMinPoolSize(5);
    cpds.setAcquireIncrement(5);
    cpds.setMaxPoolSize(20);

    Database.forDataSource(cpds) //Should be changed (in memory)
  }

}
