/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: Stop_Result_Reader_Exception.java
Created on: Nov 7, 2020 7:34:53 AM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.exception;

import wasys.lib.evelyn.api.SQL_Result_Reader;

/**
 * This exception could be thrown by user, to inform Evelyn to close the working
 * SQL resources, and ignore processing next record.
 *
 *
 *
 * Please see
 * {@link SQL_Result_Reader#row_result(java.lang.Object, java.sql.ResultSet)}
 * for more info.
 *
 * @author https://github.com/911992
 */
public class Stop_Result_Reader_Exception extends Exception {

  /**
   * False, for peaceful stop
   */
  private boolean forward_the_exception_to_caller;

  public boolean isForward_the_exception_to_caller() {
	return forward_the_exception_to_caller;
  }

  /**
   * Creates an instance with {@link #forward_the_exception_to_caller} as
   * {@code false} value.
   *
   * Please see {@link #Stop_Result_Reader_Exception(boolean) }
   */
  public Stop_Result_Reader_Exception() {
	this(false);
  }

  /**
   *
   * @param forward_the_exception_to_caller when true, exception won't be
   * consumed by Evelyn, instead it will be thrown to the caller(user).
   */
  public Stop_Result_Reader_Exception(boolean forward_the_exception_to_caller) {
	this(forward_the_exception_to_caller, null);
  }

  /**
   *
   * @param arg_forward_exception when true, the exception will be throws by
   * Evelyn to the query runner caller, false to just stop the query
   * @param string
   */
  public Stop_Result_Reader_Exception(boolean arg_forward_exception, String string) {
	this(arg_forward_exception, string, null);
  }

  public Stop_Result_Reader_Exception(boolean arg_forward_exception, String string, Throwable thrwbl) {
	super(string, thrwbl);
	this.forward_the_exception_to_caller = arg_forward_exception;
  }

}
