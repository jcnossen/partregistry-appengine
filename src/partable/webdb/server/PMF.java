package partable.webdb.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public final class PMF {
	private static final PersistenceManagerFactory pmfInstance =
		JDOHelper.getPersistenceManagerFactory("transactions-optional");

	private static PersistenceManager managerInstance = null;
	private static int managerRefCount = 0;

	private PMF() {
	}

	public static PersistenceManager get() {
		if (managerRefCount == 0)
			managerInstance = pmfInstance.getPersistenceManager(); 
		managerRefCount ++;
		return managerInstance;
	}

	/**
	 * Calls PersistanceManager.close() when the reference count is zero. 
	 */
	public static void release() {
		if (--managerRefCount == 0)
			managerInstance.close();
	}
}