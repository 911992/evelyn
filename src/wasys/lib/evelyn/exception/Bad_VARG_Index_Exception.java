/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: Bad_VARG_Index_Exception.java
Created on: Aug 26, 2020 2:26:05 PM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.exception;

/**
 * This exception is thrown by
 * {@link Evelyn_Core#compile_cmd(java.lang.String, java.lang.Class)} or
 * {@link Evelyn_Core#compile_cmd(java.lang.String, wasys.lib.java_type_util.reflect.type_sig.Type_Signature)}
 * when given index-based place-holders are not sequential or were not started
 * from zero.
 *
 * For example, considering a sql command like {@code select $1$}, where there
 * is no {@code $0$}, or {@code select $2$ + $0$} where {@code $1$} is missed
 *
 * @author https://github.com/911992
 */
public class Bad_VARG_Index_Exception extends Exception {

  public Bad_VARG_Index_Exception(String arg_msg) {
	super(arg_msg);
  }

}
