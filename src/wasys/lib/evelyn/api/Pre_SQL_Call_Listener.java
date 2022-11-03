/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: Pre_SQL_Call_Listener.java
Created on: Jan 11, 2021 4:01:51 PM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Called by the core({@link Evelyn_SQL_Core}) before a SQL call gets performed.
 * <p>
 * The listener is called before and after placeholders are set(prepared
 * statement)
 * </p>
 * <p>
 * Useful for situations when a dedicated/complex action should be done
 * </p>
 *
 * @author https://github.com/911992
 */
public interface Pre_SQL_Call_Listener {

  /**
   * Called once all statement preparation/placeholding is done(before call for
   * SQL).
   *
   * @param arg_conn the connection is associated to the op
   * @param arg_statement the related statement
   */
  public void pre_before_sql_call(Connection arg_conn, PreparedStatement arg_statement) throws SQLException;

  /**
   * Called before any statement preparation/placeholding is done
   *
   * @param arg_conn the connection is associated to the op
   * @param arg_statement the related statement
   */
  public void pre_before_preparing(Connection arg_conn, PreparedStatement arg_statement) throws SQLException;
}
