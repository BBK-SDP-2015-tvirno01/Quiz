

import java.rmi.Remote;
import java.rmi.RemoteException;

public class QuizServiceLauncher
{

	private void launch()
	{
		// 1. If there is no security manager, start one
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new RMISecurityManager());
		}

		try
		{

			// 2. Create the registry if there is not one
			LocateRegistry.createRegistry(1099);

			// 3. Create the server object
			QuizServer server = new QuizServer();

			// 4. Register (bind) the server object on the registy.
			// The registry may be on a different machine
			String registryHost = "//localhost/";
			String serviceName = "quiz";
			Naming.rebind(registryHost + serviceName, server);

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}
}