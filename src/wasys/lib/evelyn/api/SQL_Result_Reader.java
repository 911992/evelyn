/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: SQL_Result_Reader.java
Created on: Aug 28, 2020 12:50:11 AM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.api;

import wasys.lib.evelyn.exception.Stop_Result_Reader_Exception;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This interface could be implemented to let user grab results as record
 * yielding(strem/event mode) rather all-in-once( using a {@link SQL_Result} ).
 *
 * Calling {@link SQL_Command#read_query(java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, java.lang.Object...)
 * }
 * by giving an instance of this interface will inform Evelyn to call {@link #row_result(java.lang.Object, java.sql.ResultSet)
 * } for each record is proceed.
 *
 * Please mind dat mapping(output POJO filling) still is taken the place by
 * Evelyn, unless the query is record-effective command(like insert, update,...)
 * then Related {@link #update_row_count(int) } is called instead.
 *
 * Also see {@link Raw_SQL_Result_Reader} if you wish to manage and handle raw
 * {@link ResultSet} content by your own.
 *
 * @author https://github.com/911992
 */
public interface SQL_Result_Reader<A> {

  /**
   * A singleton instance of the exception type is used for informing Evelyn to
   * stop processing rest rows, and return to caller.
   */
  public static final Stop_Result_Reader_Exception DEFAULT_STOP_QUERY_READING_EX_INSTANCE = new Stop_Result_Reader_Exception();

  /**
   * Called before related query statement is executed(pre sql-call)
   */
  default public void command_started() {
  }

  /**
   * Called when result of the sql-statement was record-effective.
   *
   * @param arg_update_count number of records affected
   */
  default public void update_row_count(int arg_update_count) {
	throw new AssertionError("Error kB192Cv: update_row_count has not been implemented yet");
  }

  /**
   * Called when a row of working {@link ResultSet} is proceed and related POJO
   * were filled.
   *
   * This function is called from {@link #row_result(java.lang.Object, java.sql.ResultSet)
   * }, if working {@link ResultSet} is required, then override that func
   * instead.
   *
   * @param arg_ins filled POJO from current result-set position.
   * @throws Stop_Result_Reader_Exception when processing next rows are not
   * required
   * @throws SQLException SQL-level exceptions could be passed to Evelyn. The
   * same exception/object will be throws to caller(user)
   */
  public void row_result(A arg_ins) throws Stop_Result_Reader_Exception, SQLException;

  /**
   * Called when a row of working {@link ResultSet} is proceed and related POJO
   * were filled.
   *
   * By default, {@link #row_result(java.lang.Object) } is called.
   *
   * If only working {@link ResultSet} is required, then implement
   * {@link Raw_SQL_Result_Reader} interface instead.
   *
   * @param arg_ins filled POJO from current result-set position.
   * @param arg_resultset working result-set at current position
   * @throws Stop_Result_Reader_Exception when processing next rows are not
   * required
   * @throws SQLException SQL-level exceptions could be passed to Evelyn. The
   * same exception/object will be throws to caller(user)
   */
  default public void row_result(A arg_ins, ResultSet arg_resultset) throws Stop_Result_Reader_Exception, SQLException {
	row_result(arg_ins);
  }

  /**
   * Called when working result-set, statement, and related connection were
   * closed.
   */
  default public void command_finished() {
  }

}
