/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: SQL_Command.java
Created on: Aug 26, 2020 5:21:03 AM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn.api;

//import java.util.ArrayList;
//import wasys.lib.java_type_util.reflect.type_sig.Type_Field_Signature;
import wasys.lib.evelyn.impl.Holder_Map;
import java.sql.SQLException;
import wasys.lib.evelyn.Evelyn_Core;
import wasys.lib.java_type_util.reflect.type_sig.Type_Signature;

/**
 * Holds required information about a compiled Evelyn command.
 *
 * Keeps track of placeholders mappings, and generated(parsed) JDVC-ready
 * command.
 *
 * Note: field are used mostly for internal (Evelyn core) functionality, and
 * changing fields may result breaking things up.
 *
 * @author https://github.com/911992
 */
public class SQL_Command<A> {

  public SQL_Command(String parsed_cmd, Type_Signature<A> pojo_typ_sig, int essential_varg_length, boolean pojo_could_be_null, Holder_Map[] holder_mapping) {
	this.parsed_cmd = parsed_cmd;
	this.pojo_typ_sig = pojo_typ_sig;
	this.essential_varg_length = essential_varg_length;
	this.pojo_could_be_null = pojo_could_be_null;
	this.holder_mapping = holder_mapping;
  }

  //includes the generated place-holdered command (ready for call)
  private final String parsed_cmd;
  //
  private final Type_Signature<A> pojo_typ_sig;
  //essential VARG len are reuqired
  private final int essential_varg_length;
  //if true, it means given pojo later for running the command could be null, since there is no need for the pojo(weird bu true)
  private final boolean pojo_could_be_null;
  //mapping of the holders in generated command
  private final Holder_Map[] holder_mapping;

  public String getParsed_cmd() {
	return parsed_cmd;
  }

  public Type_Signature<A> getPojo_typ_sig() {
	return pojo_typ_sig;
  }

  public int getEssential_varg_length() {
	return essential_varg_length;
  }

  public boolean isPojo_could_be_null() {
	return pojo_could_be_null;
  }

  public Holder_Map[] getHolder_mapping() {
	return holder_mapping;
  }

  //+run_query(arg_pojo_input:A:=null,arg_out_sig:Type_Signature<C>,VARG...Object):SQL_Result<C>
  /**
   * Calls this sql-command with given output-POJO signature, and given
   * optional(VARG) indexed args.
   *
   * Calls {@link #read_query(java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, java.lang.Object...)
   * } with {@code null} for pojo-input arg
   *
   * * See {@link Evelyn_Core#run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * } for detailed info.
   *
   * @param <C> type of output-POJO
   * @param arg_out_sig type-signature of output-pojo, could be null if related
   * sql command is a record-affective one.
   * @param VARG indexed arguments
   * @return result of query
   * @throws SQLException
   */
  public <C> SQL_Result<C> run_query(Type_Signature<C> arg_out_sig, Object... VARG) throws SQLException {
	return run_query(null, arg_out_sig, VARG);
  }

  /**
   * As same as {@link #read_query(wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, java.lang.Object...)
   * }, but exceptions are handled, and thrown by an instance of
   * {@link AssertionError}.
   *
   * * See {@link Evelyn_Core#run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * } for detailed info.
   *
   * @param <C> type of output-POJO
   * @param arg_out_sig type-signature of output-pojo, could be null if related
   * sql command is a record-affective one.
   * @param VARG indexed arguments
   * @return result of query
   */
  public <C> SQL_Result<C> run_query_assert(Type_Signature<C> arg_out_sig, Object... VARG) {
	try {
	  return run_query(arg_out_sig, VARG);
	} catch (Throwable wtf) {
	  throw new AssertionError("run_query_assert(:Type_Signature,...)", wtf);
	}
  }

  /**
   * Calls this sql-command with given input-pojo instance, output-POJO
   * signature, and given optional(VARG) indexed args.
   *
   * See {@link Evelyn_Core#run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * } for detailed info.
   *
   * @param <C> type of output-POJO
   * @param arg_pojo_input the input POJO instance (could be null if there is no
   * any field placeholder)
   * @param arg_out_sig type-signature of output-pojo, could be null if related
   * sql command is a record-affective one.
   * @param VARG indexed arguments
   * @return result of query
   * @throws SQLException
   */
  public <C> SQL_Result<C> run_query(A arg_pojo_input, Type_Signature<C> arg_out_sig, Object... VARG) throws SQLException {
	return Evelyn_Core.run_query(this, arg_pojo_input, arg_out_sig, VARG);
  }

  /**
   * Calls {@link #run_query(java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, java.lang.Object...)
   * } and throws an {@link AssertionError} in case of any error.
   *
   * See {@link Evelyn_Core#run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * } for detailed info.
   *
   * @param <C> type of output-POJO
   * @param arg_pojo_input the input POJO instance (could be null if there is no
   * any field placeholder)
   * @param arg_out_sig type-signature of output-pojo, could be null if related
   * sql command is a record-affective one.
   * @param VARG indexed arguments
   * @return
   */
  public <C> SQL_Result<C> run_query_assert(A arg_pojo_input, Type_Signature<C> arg_out_sig, Object... VARG) {
	try {
	  return run_query(arg_pojo_input, arg_out_sig, VARG);
	} catch (Throwable wtf) {
	  throw new AssertionError("run_query_assert(:A,:Type_Signature,...)", wtf);
	}
  }
//+read_query(arg_pojo_input:A=null,arg_out_sig:Type_Signature<C>:=null,SQL_Result_Reader<C>,VARG...Object):void

  /**
   * Place this sql command to be run as a stream(event based) result mode.
   * Given reader should not null.
   *
   * Calls {@link #read_query(java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, java.lang.Object...)
   * }
   *
   * See {@link Evelyn_Core#run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * } for detailed info.
   *
   * @param <C> type of output-POJO
   * @param arg_out_sig type-signature of output-pojo, could be null if related
   * sql command is a record-affective one.
   * @param arg_reader (not null) result reader object
   * @param VARG indexed arguments
   * @throws SQLException
   */
  public <C> void read_query(Type_Signature<C> arg_out_sig, SQL_Result_Reader<C> arg_reader, Object... VARG) throws SQLException {
	read_query(null, arg_out_sig, arg_reader, VARG);
  }

  public <C> void read_query(A arg_pojo_input, Type_Signature<C> arg_out_sig, SQL_Result_Reader<C> arg_reader, Object... VARG) throws SQLException {
	Evelyn_Core.read_query(this, arg_pojo_input, arg_out_sig, arg_reader, VARG);
  }

  //+read_query_rs(arg_pojo_input:A=null,:SQL_Result_Reader<C>,VARG...Object):void
  /**
   * Place this sql command to be run as a stream(event based) result mode.
   * Given reader should not null.
   *
   * Calls {@link #read_query(java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, java.lang.Object...)
   * }
   *
   * See {@link Evelyn_Core#run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * } for detailed info.
   *
   * @param <C> type of output-POJO
   * @param arg_reader (not null) result reader object
   * @param VARG indexed arguments
   * @throws SQLException
   */
  public <C> void read_query_rs(SQL_Result_Reader<C> arg_reader, Object... VARG) throws SQLException {
	read_query_rs(null, arg_reader, VARG);
  }

  /**
   * Place this sql command to be run as a stream(event based) result mode.
   * Given reader should not null.
   *
   * Calls {@link #read_query(java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, java.lang.Object...)
   * }
   *
   * See {@link Evelyn_Core#run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * } for detailed info.
   *
   * @param <C> type of output-POJO
   * @param arg_pojo_input the input POJO instance (could be null if there is no
   * any field placeholder)
   * @param arg_reader (not null) result reader object
   * @param VARG indexed arguments
   * @throws SQLException
   */
  public <C> void read_query_rs(A arg_pojo_input, SQL_Result_Reader<C> arg_reader, Object... VARG) throws SQLException {
	read_query(arg_pojo_input, null, arg_reader, VARG);
  }

  /**
   * Place this sql command to be run as a stream(event based) result mode.
   * Given reader should not null.
   *
   * Calls {@link #read_query(java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, java.lang.Object...)
   * }
   *
   * See {@link Evelyn_Core#run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * } for detailed info.
   *
   * @param <C> type of output-POJO
   * @param arg_pojo_input the input POJO instance (could be null if there is no
   * any field placeholder)
   * @param arg_out_sig type-signature of output-pojo, could be null if related
   * sql command is a record-affective one.
   * @param arg_reader (not null) result reader object
   * @param VARG indexed arguments
   * @throws SQLException
   */
  public <C> void read_query_rs(A arg_pojo_input, Type_Signature<C> arg_out_sig, SQL_Result_Reader<C> arg_reader, Object... VARG) throws SQLException {
	read_query(arg_pojo_input, arg_out_sig, arg_reader, VARG);
  }

}
