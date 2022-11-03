/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: SQL_Result.java
Created on: Aug 27, 2020 10:43:39 AM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.api;

import wasys.lib.evelyn.Evelyn_Core;
import java.util.ArrayList;

/**
 * The entity type is used to carry result of a {@link SQL_Command} call.
 *
 * Regardless if the related query returned some data, or not(like
 * record-effective), an instance of this class is created.
 *
 * Evelyn uses a default object-factory to create instance of this class when
 * required, and it can be replaced, see {@link Evelyn_Core#set_sql_result_factory(wasys.lib.java_type_util.reflect.type_sig.Object_Factory) }
 *
 * Type param {@code A} refers to the output POJO type, which is used for result-row array-list as well.
 * @author https://github.com/911992
 */
public class SQL_Result<A> {

  private int manipulated_rows;
  private ArrayList<A> result_rows;

//    public SQL_Result(int manipulated_rows) {
//        this.manipulated_rows = manipulated_rows;
//    }
//    public SQL_Result(ArrayList<A> result_rows) {
//        this.result_rows = result_rows;
//    }
  public SQL_Result() {
  }

  public int getManipulated_rows() {
	return manipulated_rows;
  }

  public void setManipulated_rows(int manipulated_rows) {
	this.manipulated_rows = manipulated_rows;
  }

  public ArrayList<A> getResult_rows() {
	return result_rows;
  }

  public void setResult_rows(ArrayList<A> result_rows) {
	this.result_rows = result_rows;
  }

  public boolean has_query_result() {
	return (result_rows != null && result_rows.size() > 0);
  }
}
