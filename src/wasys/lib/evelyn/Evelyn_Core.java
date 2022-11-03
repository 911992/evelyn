/*
 * Copyright (c) 2022, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
Evelyn
File: Evelyn_SQL_Core.java
Created on: Aug 26, 2020 6:40:53 AM
    @author https://github.com/911992
 
History:
    initial version: 0.1(Oct 22, 2022)
 */
package wasys.lib.evelyn;

//import com.sun.org.apache.xerces.internal.dom.AbortException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
import wasys.lib.java_type_util.reflect.type_sig.Object_Factory;
import wasys.lib.java_type_util.reflect.type_sig.Type_Field_Signature;
import wasys.lib.java_type_util.reflect.type_sig.Type_Parser;
import wasys.lib.java_type_util.reflect.type_sig.Type_Signature;
import wasys.lib.java_type_util.reflect.type_sig.Type_Signature_Parse_Policy;
import wasys.lib.evelyn.impl.*;
import wasys.lib.evelyn.api.*;
import wasys.lib.evelyn.api.annotation.*;
import wasys.lib.evelyn.exception.*;

/**
 * This is the core class of Evelyn, which handles both command compiling and
 * running compiled commands.
 *
 * This class is stateless. For compiling a command please check out {@code #compile_cmd()
 * } funcs.
 *
 * For running a query, user may use the given {@link SQL_Command} object, or
 * call {@code run_query_varg()} funcs(or its aliases).
 *
 * Before running any {@link SQL_Command}, mind working Connection must be set
 * by {@link #set_working_db_factory(wasys.lib.java_type_util.reflect.type_sig.Object_Factory)
 * } function.
 *
 * @author https://github.com/911992
 */
public class Evelyn_Core {

  /**
   * Default type signature is used when no input POJO type is provided during
   * command-compiling.
   */
  private static final Type_Signature<Object> THE_VOID_TYPE_SIG = Type_Parser.parse_no_filter(Object.class, Type_Signature_Parse_Policy.DEFAULT_POLICY);

  private Evelyn_Core() {
  }

  /**
   * For escaping. Mostly for placeholders.
   */
  public static final char ESCAPE_CHAR = '\\';

  /**
   * The char is used for POJO field placeholders. (both head and tail)
   */
  public static final char POJO_FIELD_MARKER = '&';

  /**
   * The char is used for VARG index placeholders. (both head and tail)
   */
  public static final char VARG_MARKER = '$';

  /**
   * Default type signature policy which is used for generating the
   * type-signature of related input POJO class.
   *
   * See {@link #compile_cmd(java.lang.String, java.lang.Class) }
   */
  public static final Type_Signature_Parse_Policy DEFAULT_TYPE_SIG_POLICY = new Type_Signature_Parse_Policy(Type_Signature_Parse_Policy.ACCESS_SPECIFIER_ALL, true, false);

//    private static DB_Conn_Factory working_db_factory;
  private static Object_Factory<Connection> WORKING_DB_FACTORY;

  /**
   * Working Object-Factory for making instances of {@link SQL_Result} when
   * required.
   *
   * See {@link #set_sql_result_factory(wasys.lib.java_type_util.reflect.type_sig.Object_Factory)
   * }
   */
  private static Object_Factory<SQL_Result<Object>> SQL_RESULT_FACTORY = null;

  /**
   * Finds and returns the field-signature named as given
   * {@code arg_field_name}.
   *
   * @param arg_field_name name of the field to be searched
   * @param arg_fields fields to be searched from
   * @return index of field-signature object of given array, or -1(not found)
   */
  static final int field_name_idx(String arg_field_name, ArrayList<Type_Field_Signature> arg_fields) {
	Type_Field_Signature _sig;
	for (int a = 0; a < arg_fields.size(); a++) {
	  _sig = arg_fields.get(a);
	  if (_sig.get_user_meta_from_field_info_annot_or_name().equals(arg_field_name)) {
		return a;
	  }
//            if (_sig.getField().getName().equals(arg_field_name)) {
//                return a;
//            }
//            if(arg_field_name.equals(_sig.get_user_meta_from_field_info_annot())){
//                return a;
//            }
	}
	return -1;
  }

//    static public void set_working_db_factory(DB_Conn_Factory arg_db_con);
  /**
   * Sets working connection factory instance(singleton). This is required to
   * running a {@link SQL_Command}.
   *
   * Mind {@link Evelyn_Core} is stateless, so given connection factory will be
   * used application(classloader) wide.
   *
   * @param arg_db_con
   */
  static public void set_working_db_factory(Object_Factory<Connection> arg_db_con) {
	WORKING_DB_FACTORY = arg_db_con;
  }

  /**
   * Sets the working object-factory for generating {@link SQL_Result}
   * instances. Sets {@link #SQL_RESULT_FACTORY} field.
   *
   * By default related factory class is {@code null}, and instances are
   * generated using default constructor of {@link SQL_Result}
   *
   * @param arg_fact
   */
  static public void set_sql_result_factory(Object_Factory<SQL_Result<Object>> arg_fact) {
	SQL_RESULT_FACTORY = arg_fact;
  }

  /**
   * Compiles the given command by assuming there is no any field-placeholder.
   *
   * It uses void {@link #THE_VOID_TYPE_SIG} type signature which results no any
   * field to be pointer(placeholder).
   *
   * Please see {@link #compile_cmd(java.lang.String, wasys.lib.java_type_util.reflect.type_sig.Type_Signature)
   * }
   *
   * @param arg_cmd the (field-placeholder free) command to be compiled.
   * @return
   * @throws No_Such_POJO_Field_Exception when any field-place holder were used
   * by the command
   * @throws Bad_VARG_Index_Exception when order or VARG indexed placeholders
   * are broken, or not started from zero
   */
  static public SQL_Command compile_cmd(String arg_cmd) throws No_Such_POJO_Field_Exception, Bad_VARG_Index_Exception {
	return compile_cmd(arg_cmd, THE_VOID_TYPE_SIG);
  }

  /**
   * Calls {@link #compile_cmd(java.lang.String) } by catching exceptions, and
   * throws them using an {@link AssertionError} instance.
   *
   * See {@link #compile_cmd(java.lang.String) }
   *
   * @param arg_cmd
   * @return
   */
  static public SQL_Command compile_cmd_assert(String arg_cmd) {
	try {
	  return compile_cmd(arg_cmd);
	} catch (Throwable wth) {
	  throw new AssertionError(String.format("Could not compile cmd \"%s\" (without arg_type In Entity)", arg_cmd), wth);
	}
  }

  /**
   * Compiles the given command.
   *
   * It creates a type-signature of given class, and calls {@link #compile_cmd(java.lang.String, wasys.lib.java_type_util.reflect.type_sig.Type_Signature)
   * } then.
   *
   * If the given command has no any field placeholder, then better call {@link #compile_cmd(java.lang.String)
   * } instead.
   *
   * @param <E> Type of the input POJO
   * @param arg_cmd command to be compiled
   * @param arg_pojo_class input POJO class
   * @return
   * @throws No_Such_POJO_Field_Exception when any field-place holder were used
   * by the command
   * @throws Bad_VARG_Index_Exception when order or VARG indexed placeholders
   * are broken, or not started from zero
   */
  static public <E> SQL_Command<E> compile_cmd(String arg_cmd, Class<E> arg_pojo_class) throws No_Such_POJO_Field_Exception, Bad_VARG_Index_Exception {
	Type_Signature<E> _typ_sig = Type_Parser.parse(arg_pojo_class, DEFAULT_TYPE_SIG_POLICY, new SQL_Type_Field_Filter());
	return compile_cmd(arg_cmd, _typ_sig);
  }

  /**
   * Calls {@link #compile_cmd(java.lang.String, java.lang.Class) } by catching
   * exceptions, and throws them using an {@link AssertionError} instance.
   *
   * See {@link #compile_cmd(java.lang.String, java.lang.Class) }
   *
   * @param <E> Type of the input POJO
   * @param arg_cmd command to be compiled
   * @param arg_pojo_class input POJO class
   * @return
   */
  static public <E> SQL_Command<E> compile_cmd_assert(String arg_cmd, Class<E> arg_pojo_class) {
	try {
	  return compile_cmd(arg_cmd, arg_pojo_class);
	} catch (Throwable wth) {
	  throw new AssertionError(String.format("Could not compile cmd \"%s\" for given type \"%s\"", arg_cmd, arg_pojo_class), wth);
	}
  }

  /**
   * Compiles the given command, and return the result as a {@link SQL_Command}
   * instance.
   * <p>
   * Both VARG-indexed and field placeholders are checked and validated to be
   * legit, otherwise an exception is thrown.
   * <p>
   * Please note given SQL command is not checked to be correct by a(working)
   * {@link Connection} object, so no {@link SQLException} throwing at this
   * level.
   * <p>
   * Order of VARG-indexed placeholders no matter, and an indexed placeholder
   * could be used multiple times in given command. Mind VARG-indexed
   * placeholders must be started from zero and identically incrementing by +1.
   * Any missed index(like no index 1, but 0 and 2) will resulting an exception
   * to be thrown.
   * <p>
   * All field-placeholders are checked, and they all must be available by given
   * type-signature(class), otherwise, related exception will be thrown.
   * <p>
   * Examples:
   * <ul>
   * <li>{@code select * from cool_schema.mighty_table limit $0$ offset $1$}</li>
   * <li>{@code select flags from c.coords where hash = &hash& }</li>
   * <li>{@code select count(*)::int from e_logs where $0$ = &check_field&}</li>
   * <li>{@code select current_timestamp + '$0$ hours'}</li>
   * </ul>
   * <p>
   * Using {@code \} for escaping, like
   * {@code select '\&not_a_field\&','\$666\$'}
   * <p>
   * All Evelyn-level placeholders will be replaced with std JDBC {@code ?}
   * placeholder to make the command ready to be called.
   * <p>
   * Hint: user may check out {@link SQL_Command#getParsed_cmd() } for debugging
   * purposes.
   *
   * @param <E> Type of the input POJO
   * @param arg_cmd command to be compiled (not null)
   * @param arg_pojo_sig input POJO class's type-signature(not null)
   * @return
   * @throws No_Such_POJO_Field_Exception when any field-place holder were used
   * by the command
   * @throws Bad_VARG_Index_Exception when order or VARG indexed placeholders
   * are broken, or not started from zero
   */
  static public <E> SQL_Command<E> compile_cmd(String arg_cmd, Type_Signature<E> arg_pojo_sig) throws No_Such_POJO_Field_Exception, Bad_VARG_Index_Exception {
	StringBuilder _compiled_cmd = new StringBuilder(arg_cmd.length());
//        int _essential_varg_len=0;
	ArrayList<Holder_Map> _holders = new ArrayList<>(7);
	char _c;
	boolean _escaping = false;
	int _pojo_start_idx = -1;
	int _varg_start_idx = -1;
	String _field_name;
	int _field_idx;
	int _varg_idx;
	boolean _nullable_pojo_for_run = true;
	ArrayList<Integer> _vargs = new ArrayList<>(7);
	for (int a = 0; a < arg_cmd.length(); a++) {
	  _c = arg_cmd.charAt(a);
	  if (_escaping) {
		_compiled_cmd.append(_c);
		_escaping = false;
		continue;
	  }
	  if (_c == ESCAPE_CHAR) {
		_escaping = true;
		continue;
	  }
	  if (_c == POJO_FIELD_MARKER) {
		if (_pojo_start_idx != -1) {
		  _field_name = arg_cmd.substring(_pojo_start_idx + 1, a);
		  _field_idx = field_name_idx(_field_name, arg_pojo_sig.getProceed_fields());
		  if (_field_idx == -1) {
			throw new No_Such_POJO_Field_Exception(String.format("Field \"%s\" (at %d) is not present from given %s type", _field_name, _pojo_start_idx, arg_pojo_sig.getType().getCanonicalName()));
		  }
		  _compiled_cmd.append("?");
		  _holders.add(new Holder_Map(false, _field_idx));
		  _pojo_start_idx = -1;
		  _nullable_pojo_for_run = false;
		} else {
		  _pojo_start_idx = a;
		}
		continue;
	  }
	  if (_c == VARG_MARKER) {
		if (_varg_start_idx != -1) {
		  _field_name = arg_cmd.substring(_varg_start_idx + 1, a);
		  try {
			_varg_idx = Integer.parseInt(_field_name);
		  } catch (NumberFormatException e) {
			throw new Bad_VARG_Index_Exception(String.format("VARG index \"%s\" (at %d) has invalid parsable integer value", _field_name, _varg_start_idx));
		  }
		  if (_vargs.contains(_varg_idx) == false) {
			_vargs.add(_varg_idx);
		  }
		  _compiled_cmd.append("?");
		  _holders.add(new Holder_Map(true, _varg_idx));
		  _varg_start_idx = -1;
		} else {
		  _varg_start_idx = a;
		}
		continue;
	  }
	  if (_pojo_start_idx != -1 || _varg_start_idx != -1) {
		continue;
	  }
	  _compiled_cmd.append(_c);
	}
	System.out.println(_compiled_cmd.toString());
	_vargs.sort((o1, o2) -> {
	  return Integer.compare(o1, o2);
	});
	for (int a = 0; a < _vargs.size(); a++) {
	  if (_vargs.get(a) != a) {
		throw new Bad_VARG_Index_Exception(String.format("Non-sequential VARG mapping, expected %d", a));
	  }
	}
	Holder_Map _hlds_arr[] = new Holder_Map[_holders.size()];
	_hlds_arr = _holders.toArray(_hlds_arr);
	SQL_Command<E> _res = new SQL_Command(_compiled_cmd.toString(), arg_pojo_sig, _vargs.size(), _nullable_pojo_for_run, _hlds_arr);
	return _res;
  }

  /**
   * Places the given sql-command and asks for a result ({@link SQL_Result}).
   *
   * Forwards the call to {@link #run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * }
   *
   * Uses {@link #WORKING_DB_FACTORY} as connection factory for calling
   * {@code run_query_varg()}.
   *
   * @param <C>
   * @param <E>
   * @param arg_cmd
   * @param arg_out_sig
   * @param VARG
   * @return
   * @throws SQLException
   */
  public static <C, E> SQL_Result<C> run_query(SQL_Command<E> arg_cmd, Type_Signature<C> arg_out_sig, Object... VARG) throws SQLException {
	return run_query(arg_cmd, null, arg_out_sig, VARG);
  }

  /**
   * Places the given sql-command and asks for a result ({@link SQL_Result}).
   *
   * Forwards the call to {@link #run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * }
   *
   * Uses {@link #WORKING_DB_FACTORY} as connection factory for calling
   * {@code run_query_varg()}.
   *
   * @param <C>
   * @param <E>
   * @param arg_cmd
   * @param arg_pojo_input
   * @param arg_out_sig
   * @param VARG
   * @return
   * @throws SQLException
   */
  public static <C, E> SQL_Result<C> run_query(SQL_Command<E> arg_cmd, E arg_pojo_input, Type_Signature<C> arg_out_sig, Object... VARG) throws SQLException {
	return run_query_varg(arg_cmd, arg_pojo_input, arg_out_sig, null, WORKING_DB_FACTORY, VARG);
  }

  /**
   * Places the given sql-command and asks for streaming the result to given
   * result-reader {@code arg_reader}
   *
   * Forwards the call to {@link #run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * }
   *
   * Uses {@link #WORKING_DB_FACTORY} as connection factory for calling
   * {@code run_query_varg()}.
   *
   * @param <C>
   * @param <E>
   * @param arg_cmd
   * @param arg_out_sig
   * @param arg_reader
   * @param VARG
   * @throws SQLException
   */
  public static <C, E> void read_query(SQL_Command<E> arg_cmd, Type_Signature<C> arg_out_sig, SQL_Result_Reader<C> arg_reader, Object... VARG) throws SQLException {
	read_query(arg_cmd, null, arg_out_sig, arg_reader, VARG);
  }

  /**
   * Places the given sql-command and asks for streaming the result to given
   * result-reader {@code arg_reader}
   *
   * Forwards the call to {@link #run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * }
   *
   * Uses {@link #WORKING_DB_FACTORY} as connection factory for calling
   * {@code run_query_varg()}.
   *
   * @param <C>
   * @param <E>
   * @param arg_cmd
   * @param arg_pojo_input
   * @param arg_out_sig
   * @param arg_reader
   * @param VARG
   * @throws SQLException
   */
  public static <C, E> void read_query(SQL_Command<E> arg_cmd, E arg_pojo_input, Type_Signature<C> arg_out_sig, SQL_Result_Reader<C> arg_reader, Object... VARG) throws SQLException {
	run_query_varg(arg_cmd, arg_pojo_input, arg_out_sig, arg_reader, WORKING_DB_FACTORY, VARG);
  }

  /**
   * Places the given sql-command and asks for streaming the result to given
   * result-reader {@code arg_reader}
   *
   * Forwards the call to {@link #run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * }
   *
   * Uses {@link #WORKING_DB_FACTORY} as connection factory for calling
   * {@code run_query_varg()}.
   *
   * @param <C>
   * @param <E>
   * @param arg_cmd
   * @param arg_reader
   * @param VARG
   * @throws SQLException
   */
  public static <C, E> void read_query_rs(SQL_Command<E> arg_cmd, Raw_SQL_Result_Reader<C> arg_reader, Object... VARG) throws SQLException {
	read_query_rs(arg_cmd, null, arg_reader, VARG);
  }

  /**
   * Places the given sql-command and asks for streaming the result to given
   * result-reader {@code arg_reader}
   *
   * Forwards the call to {@link #run_query_varg(wasys.lib.evelyn.api.SQL_Command, java.lang.Object, wasys.lib.java_type_util.reflect.type_sig.Type_Signature, wasys.lib.evelyn.api.SQL_Result_Reader, wasys.lib.java_type_util.reflect.type_sig.Object_Factory, java.lang.Object...)
   * }
   *
   * Uses {@link #WORKING_DB_FACTORY} as connection factory for calling
   * {@code run_query_varg()}.
   *
   * @param <C>
   * @param <E>
   * @param arg_cmd
   * @param arg_pojo_input
   * @param arg_reader
   * @param VARG
   * @throws SQLException
   */
  public static <C, E> void read_query_rs(SQL_Command<E> arg_cmd, E arg_pojo_input, Raw_SQL_Result_Reader<C> arg_reader, Object... VARG) throws SQLException {
	run_query_varg(arg_cmd, arg_pojo_input, null, arg_reader, WORKING_DB_FACTORY, VARG);
  }

  /**
   * Places(runs) the given sql-command, either calls the given
   * {@code arg_reader}, or return the result by a {@link SQL_Result} instance.
   * <p>
   * Given VARG array-length must be equal to expected indexed placeholders
   * count, otherwise an {@link IndexOutOfBoundsException} is thrown.
   * <p>
   * Same for {@code arg_pojo_input}, could not be null, if there is any field
   * placeholder is needed, otherwise should be null.
   * <p>
   * A {@link Connection} object is asked to be created from given
   * {@link arg_db_factory} which is probably points out to default working
   * connection-factory({@link #WORKING_DB_FACTORY}) object.
   * <p>
   * Given {@code arg_pojo_input} could be a {@link Pre_SQL_Call_Listener} as
   * well, and if yes, related callbacks are called before and after
   * filling/streaming ops are done. (please see {@link Pre_SQL_Call_Listener})
   * <p>
   * Once all placeholders are set, prepared statement is executed. Mind
   * throwing a {@link SQLException} is possible, as calling command has some
   * issues.
   * <p>
   * Once statement is executed successfully, and no result reader
   * ({@code arg_reader}) was given, then a new {@link SQL_Result} instance will
   * be created by calling {@link #new_sql_res_instance(wasys.lib.java_type_util.reflect.type_sig.Type_Signature, boolean)
   * }, otherwise (when {@code arg_reader} is not null), results will be
   * streamed to provided reader. will be used.
   * <p>
   * Note: If given {@code arg_reader} is an instance of
   * {@link Raw_SQL_Result_Reader}, then object-mapping to related output-POJO
   * will be ignored.
   * <p>
   * If executed statement returned some data, then given output POJO
   * type-signature(associated object-factory)is used for creating instances of
   * output POJO for each working cursor({@link ResultSet}) position.
   * <p>
   * If a {@link Stop_Result_Reader_Exception} exception is thrown by user(given
   * {@code arg_reader}) and if it were asked to forward it to the caller, then
   * beside ignoring processing rest of data, an {@link IllegalStateException}
   * exception is thrown as well.
   * <p>
   *
   * @param <C>
   * @param <E>
   * @param arg_cmd (non null) the command needs to be run
   * @param arg_pojo_input input pojo instance for field-place holders. could be
   * null if there is no any. Could be a {@link Pre_SQL_Call_Listener} as well.
   * @param arg_out_sig type signature of output-pojo. could be null if query
   * won't advance any result(like record affected), or {@code arg_reader} is an
   * instance of {@link Raw_SQL_Result_Reader}
   * @param arg_reader (could be null) result reader, when
   * @param arg_db_factory (not null) the jdbc {@link Connection} factory
   * @param VARG indexed placeholders (must be exactly as many as expected in
   * sql command)
   * @return null when {@code arg_reader} arg is not null, otherwise result of
   * called query
   * @throws SQLException
   * @throws IndexOutOfBoundsException when len of given VARG array is not equal
   * to expected indexed args of related sql-command({@code arg_cmd})
   * @throws IllegalStateException when given {@code arg_pojo_input} is null,
   * but sql-command depends on field-placeholders. Or when user defined result
   * reader({@code arg_reader}) threw a {@link Stop_Result_Reader_Exception} and
   * asked to forward the exception to the caller
   */
  public static <C, E> SQL_Result<C> run_query_varg(SQL_Command<E> arg_cmd, E arg_pojo_input, Type_Signature<C> arg_out_sig, SQL_Result_Reader<C> arg_reader, Object_Factory<Connection> arg_db_factory, Object... VARG) throws SQLException {
	int _essential_varg = arg_cmd.getEssential_varg_length();
	if (VARG.length != _essential_varg) {
	  throw new IndexOutOfBoundsException(String.format("VARG array-len mismatch. Count of given VARG args(%d) for indexed placeholders must be %d", VARG.length, _essential_varg));
	}
	if (arg_cmd.isPojo_could_be_null() == false && arg_pojo_input == null) {
	  throw new IllegalStateException("Given input-pojo cannot be null, as sql-command depends on field-placeholder(s)");
	}
	SQL_Result<C> _res = null;
	try ( Connection _conn = arg_db_factory.create_object(Connection.class);  PreparedStatement _ps = _conn.prepareStatement(arg_cmd.getParsed_cmd())) {
	  if (arg_pojo_input instanceof Pre_SQL_Call_Listener) {
		((Pre_SQL_Call_Listener) arg_pojo_input).pre_before_preparing(_conn, _ps);
	  }
//	  if (arg_pojo_input instanceof Cursorable) {
//		int _cur_size = ((Cursorable) arg_pojo_input).cursor_fetch_size();
//		if (_cur_size > 0) {
//		  _conn.setAutoCommit(((Cursorable) arg_pojo_input).autocommit_on_cursor());
//		  _ps.setFetchSize(_cur_size);
//		}
//	  }
	  Holder_Map[] _maps = arg_cmd.getHolder_mapping();
	  ArrayList<Type_Field_Signature> _pojo_fields = arg_cmd.getPojo_typ_sig().getProceed_fields();
	  Type_Field_Signature _fsig;
	  Array_Type _arr_type;
	  if (_maps != null) {
		Holder_Map _holder;
		for (int a = 0; a < arg_cmd.getHolder_mapping().length; a++) {
		  _holder = _maps[a];
		  Object _obj;
		  if (_holder.isAs_varg()) {
			_obj = VARG[_holder.getIndex()];
			_fsig = null;
		  } else {
			_fsig = _pojo_fields.get(_holder.getIndex());
			_obj = _pojo_fields.get(_holder.getIndex())
					.get(arg_pojo_input);
		  }
		  if (_obj == null) {
			_ps.setNull(a + 1, java.sql.Types.OTHER);
		  } else {
			if (_obj instanceof List && _fsig != null
					&& (_arr_type = _fsig.getField().getAnnotation(Array_Type.class)) != null) {
			  _obj = get_array_for_ls(_arr_type.array_sql_type(), (List) _obj, _conn);
			}
			_ps.setObject(a + 1, _obj);
		  }
		}
	  }
	  if (arg_reader != null) {
		arg_reader.command_started();
	  }
	  if (arg_pojo_input instanceof Pre_SQL_Call_Listener) {
		((Pre_SQL_Call_Listener) arg_pojo_input).pre_before_sql_call(_conn, _ps);
	  }
	  boolean _exec = _ps.execute();
	  /*as query(true)*/
	  if (_exec) {
		Type_Field_Signature _ctsig;
		try ( ResultSet _rs = _ps.getResultSet();) {
		  Raw_SQL_Result_Reader<C> _raw_reader = null;
		  if (arg_reader instanceof Raw_SQL_Result_Reader) {
			_raw_reader = (Raw_SQL_Result_Reader<C>) arg_reader;
		  }
		  if (_raw_reader != null && _raw_reader.need_raw_resultset()) {
			_raw_reader.query_result_set(_rs);
		  } else {
			ArrayList<Type_Field_Signature> _c_fields = arg_out_sig.getProceed_fields();
			if (arg_reader == null) {
			  _res = new_sql_res_instance(arg_out_sig, true);
			}
			Object[] _col_names = get_result_field_names(_rs.getMetaData());
			Object _col_name_idx;
			int _col_idx;
			for (int a = 0; a < _c_fields.size(); a++) {
			  _ctsig = _c_fields.get(a);
//                            _col_idx = col_index(_ctsig.getField().getName(), _col_names);
			  _col_idx = col_index(_ctsig, _col_names);
			  if (_col_idx != -1) {
				_col_names[_col_idx] = a;
			  }
			}
			boolean _field_null_push;
			while (_rs.next()) {
			  C _cins = arg_out_sig.create_object();
			  for (int a = 0; a < _col_names.length; a++) {
				_col_name_idx = _col_names[a];
				if ((_col_name_idx instanceof String)) {
				  continue;
				}
				_ctsig = _c_fields.get((int) _col_name_idx);
				_field_null_push = _ctsig.getField().getAnnotation(No_Set_Null_Object.class) == null;
				Object _rs_val = _rs.getObject(_ctsig.get_user_meta_from_field_info_annot_or_name());
				if (_rs_val instanceof Array && List.class.isAssignableFrom(_ctsig.getField().getType())) {
				  List _ls = (List) _ctsig.get(_cins);
				  try ( ResultSet _arr_rs = ((Array) _rs_val).getResultSet()) {
					Object _read;
					while (_arr_rs.next()) {
					  _read = _arr_rs.getObject(2);
					  if (_arr_rs.wasNull()) {
						if (_field_null_push) {
						  _ls.add(null);
						}
					  } else {
						_ls.add(_read);
					  }

					}
				  }
				} //                            if (_rs_val != null) 
				else {
				  if (_rs.wasNull()) {
					if (_field_null_push) {
					  _ctsig.set(_cins, null);
					}
				  } else {
					_ctsig.set(_cins, _rs_val);
				  }
//                                    if(_rs.wasNull() && !_field_null_push){
//                                        continue;
//                                    }

				}
			  }
//                            for (int a = 0; a < _c_fields.size(); a++) {
//                                _ctsig = _c_fields.get(a);
//                                if(col_index(_ctsig.getField().getName(), _col_names) == false){
//                                    continue;
//                                }
//                                Object _rs_val = _rs.getObject(_ctsig.getField().getName());
//    //                            if (_rs_val != null) 
//                                {
//                                    _ctsig.set(_cins, _rs_val);
//                                }
//                            }
			  if (arg_reader != null) {
				try {
				  arg_reader.row_result(_cins, _rs);
				} catch (Stop_Result_Reader_Exception wtf) {
				  if (wtf.isForward_the_exception_to_caller()) {
					throw new IllegalStateException("Query run were canceled/stoped by the reader", wtf);
				  }
				  break;
				}

			  } else {
				_res.getResult_rows().add(_cins);
			  }
			}
		  }

		}
	  } else {
		int _count = _ps.getUpdateCount();
		if (arg_reader != null) {
		  arg_reader.update_row_count(_count);
		} else {
		  _res = new_sql_res_instance(arg_out_sig, false);
		  _res.setManipulated_rows(_count);
		}
	  }
	}
	if (arg_reader != null) {
	  arg_reader.command_finished();
	}
	return _res;
  }

  /**
   * Returns a {@link SQL_Result} instance.
   *
   * If local {@link #SQL_RESULT_FACTORY} object factory is not {@code null},
   * then it's called for a new instance, otherwise a new instance(using default
   * const) is created.
   *
   * See {@link #set_sql_result_factory(wasys.lib.java_type_util.reflect.type_sig.Object_Factory)
   * }
   *
   * @param <C>
   * @param arg_out_sig
   * @param arg_init_list
   * @return
   */
  private static <C> SQL_Result<C> new_sql_res_instance(Type_Signature<C> arg_out_sig, boolean arg_init_list) {
	SQL_Result<C> _res;
	if (SQL_RESULT_FACTORY != null) {
	  Class _the_c = null;
	  if (arg_out_sig != null) {
		_the_c = arg_out_sig.getType();
	  }
	  _res = (SQL_Result<C>) SQL_RESULT_FACTORY.create_object(_the_c);
	} else {
	  _res = new SQL_Result();
	  if (arg_init_list) {
		_res.setResult_rows(new ArrayList());
	  }
	}
	return _res;
  }

  /**
   * Returns column labels(not names) from given resultset metadata object.
   *
   * Returning array is required for mapping resultset's column labels to
   * related output POJO's field (index mapping)
   *
   * @param arg_meta
   * @return
   * @throws SQLException
   */
  private static Object[] get_result_field_names(ResultSetMetaData arg_meta) throws SQLException {
	Object[] _res = new Object[arg_meta.getColumnCount()];
	for (int a = 0; a < _res.length; a++) {
	  _res[a] = arg_meta.getColumnLabel(a + 1);
	}
	return _res;
  }

  /**
   * Finds the index of item from given array(column's name) that points to name
   * of the asked field.
   *
   *
   * @param arg_col
   * @param arg_cols
   * @return -1 when field-name could not be found
   */
  private static int col_index(Type_Field_Signature arg_col, Object[] arg_cols) {
	Object _o;
	for (int a = 0; a < arg_cols.length; a++) {
	  _o = arg_cols[a];
//            if (arg_col.equals(_o)) {
//                return a;
//            }
	  if (arg_col.getField().getName().equals(_o)) {
		return a;
	  }
	  if (_o.equals(arg_col.get_user_meta_from_field_info_annot())) {
		return a;
	  }

	}
	return -1;
  }

  /**
   * Creates a JDBC {@link Array} of given object-array, and connection.
   *
   * Possible sql exception when concreted connection(jdbc driver) doesn't
   * support for arrays.
   *
   * @param arg_type name of the SQL type (like bigint, short, etc...)
   * @param arg_ls
   * @param arg_conn
   * @return
   * @throws SQLException
   */
  static private Array get_array_for_ls(String arg_type, List arg_ls, Connection arg_conn) throws SQLException {
	Object[] _arr = new Object[arg_ls.size()];
	arg_ls.toArray(_arr);
//        Array _res = arg_conn.createArrayOf(get_sql_type(arg_type), _arr);
	Array _res = arg_conn.createArrayOf(arg_type, _arr);
	return _res;
  }

}
