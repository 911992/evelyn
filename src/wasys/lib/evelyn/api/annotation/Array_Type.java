/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: Array_Type.java
Created on: Nov 17, 2020 5:40:17 PM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * This annotation could be used for input-POJO's fields to inform Evelyn to
 * treat the related {@link List} field with what kind of SQL type.
 *
 * This annotation MUST BE given to fields are type {@link List} (or any impl
 * sub-class)
 *
 * Note: This annotation is just for input-POJOs.
 *
 * Consider following example
 * <pre>
 * {@code
 * public class Param_Entity{
 *  Array_Type(array_sql_type="text")
 *	public ArrayList<String> all_names;
 *
 *  Array_Type(array_sql_type="bigint")
 *	public ArrayList<Long> all_flags;
 * }
 * }
 * </pre>
 *
 * @author https://github.com/911992
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Array_Type {

  /**
   * SQL type, not the java, like bigint, int, etc...
   *
   * Given type must be supported by target JDBC driver. It should not be a SQL
   * std essentially, like jsonb for postgres
   *
   * @return
   */
  public String array_sql_type();
}
