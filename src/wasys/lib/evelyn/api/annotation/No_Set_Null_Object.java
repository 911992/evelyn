/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: No_Set_Null_Object.java
Created on: Nov 16, 2020 12:24:49 PM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation could be used for output-POJO's fields to inform Evelyn
 * ignore setting the field(either directly or by setter method) when related
 * value of resultset was {@code null}.
 *
 * Anotehr option is ocntrolling the value set using related setter method.
 * Consider following example.
 *
 * <pre>
 * {@code
 * class Dudes_Pojo{
 *  //field won't be set(ignored) when related value was a null
 *	@No_Set_Null_Object private int config_flag;
 * }
 * }
 * </pre>
 *
 * @author https://github.com/911992
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface No_Set_Null_Object {

}
