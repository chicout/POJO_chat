package chat.persistence;

import javax.sql.DataSource;

/**
 * Abstract class of all the Dao classes.
 * 
 * @author Pavel Shitikov
 * 
 */
abstract class AbstractDao {

	protected final DataSource ds;

	/**
	 * Constructor that initializes <code>DataSource</code> field.
	 */
	AbstractDao(DataSource ds) {
		this.ds = ds;
	}

}
