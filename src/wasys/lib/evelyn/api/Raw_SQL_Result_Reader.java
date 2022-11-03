/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: Raw_SQL_Result_Reader.java
Created on: Aug 28, 2020 1:14:37 AM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.api;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Sub type of {@link SQL_Result_Reader} for informing Evelyn to pass the
 * {@link ResultSet}, instead of POJO filling.
 *
 * NOTE: Mind {@link #need_raw_resultset() } returns {@code true} by default
 * (and should be) for getting the {@link ResultSet}
 *
 * @author https://github.com/911992
 */
public interface Raw_SQL_Result_Reader<A> extends SQL_Result_Reader<A> {

  @Override
  default public void command_finished() {
  }

  @Override
  default public void update_row_count(int arg_update_count) {
  }

  @Override
  default public void row_result(A arg_ins) {
  }

  /**
   * Returns {@code true}(by default) if {@link ResultSet} of working statement
   * is required.
   *
   * Please see {@link #query_result_set(java.sql.ResultSet) }
   */
  default public boolean need_raw_resultset() {
	return true;
  }

  /**
   * Is called from Evelyn, to give related {@link ResultSet} (as arg).
   *
   * Note: By calling this function, any POJO filling will be ignored.
   *
   * Function {@link #need_raw_resultset() } is called first, and if it returns
   * a {@code true} value, then working {@link ResultSet} is given to the
   * user(this func).
   *
   * Mind given {@link ResultSet} could or could not be closed(better not), as
   * Evelyn will close it once function returns.
   *
   * Any thrown exception will be forwarded to the caller, or exception could be
   * handled here as well.
   *
   * @param arg_rs non null result-set of related call
   * @throws SQLException
   */
  public void query_result_set(ResultSet arg_rs) throws SQLException;
}
