/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: No_Such_POJO_Field_Exception.java
Created on: Aug 26, 2020 10:20:41 AM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.exception;

import wasys.lib.evelyn.Evelyn_Core;

/**
 * This exception is thrown by
 * {@link Evelyn_Core#compile_cmd(java.lang.String, java.lang.Class)} or
 * {@link Evelyn_Core#compile_cmd(java.lang.String, wasys.lib.java_type_util.reflect.type_sig.Type_Signature)}
 * when given input-pojo lacks a field were pointed as pojo-field placeholder of
 * given SQL-command.
 *
 * For example, considering a palceholder as {@code &my_field&}, where there is
 * no any field named {@code my_field} with given class/type-signature.
 *
 * @author https://github.com/911992
 */
public class No_Such_POJO_Field_Exception extends Exception {

  public No_Such_POJO_Field_Exception(String msg) {
	super(msg);
  }

}
