/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: Holder_Map.java
Created on: Aug 26, 2020 5:10:09 AM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.impl;

//import wasys.lib.evelyn.api.Statement_Placeholder_Setter;

/**
 * An aux/entity class is used by internal Evelyn core functionality. Keeps SQL
 * placeholder index to related input-pojo, or index-based Evelyn-level
 * placeholders.
 *
 * @author https://github.com/911992
 */
public class Holder_Map {

  /**
   * when true, means this holder points to a index-based value(like $0$)
   */
  private boolean as_varg;
  private int index;
//  private Statement_Placeholder_Setter holder_setter;

  public Holder_Map(boolean as_varg, int index) {
	this.as_varg = as_varg;
	this.index = index;
//	this(as_varg, index, new Generic_Statement_Placeholder_Setter());
  }

//  public Holder_Map(boolean as_varg, int index, Statement_Placeholder_Setter holder_setter) {
//	this.as_varg = as_varg;
//	this.index = index;
//	this.holder_setter = holder_setter;
//  }

  public boolean isAs_varg() {
	return as_varg;
  }

  public void setAs_varg(boolean as_varg) {
	this.as_varg = as_varg;
  }

  public int getIndex() {
	return index;
  }

  public void setIndex(int index) {
	this.index = index;
  }

//  public Statement_Placeholder_Setter getHolder_setter() {
//	return holder_setter;
//  }
//
//  public void setHolder_setter(Statement_Placeholder_Setter holder_setter) {
//	this.holder_setter = holder_setter;
//  }

}
