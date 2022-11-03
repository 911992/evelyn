/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: SQL_Type_Field_Filter.java
Created on: Aug 26, 2020 5:58:13 AM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.impl;

import wasys.lib.java_type_util.reflect.type_sig.Field_Filter_Entity;
//import java.sql.Array;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import wasys.lib.java_type_util.reflect.type_sig.Generic_Filter;

/**
 * The default field type filter which is used by Evelyn for compiling a SQL
 * command(input POJO). By default, all fields are considered.
 *
 * @author https://github.com/911992
 */
public class SQL_Type_Field_Filter implements Generic_Filter<Field_Filter_Entity> {

  @Override
  public boolean consider(Field_Filter_Entity arg_obj) {
	//Considering any kind of field as supported, where [set/get]Object does the job by the jdbc driver
	return true;
  }

}
