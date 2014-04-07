

import java.rmi.Remote;
import java.rmi.RemoteException;

public class AddQuizPlayerClient
{

	public static void main(String args[])
	{
		// 1. If there is no security manager, start one
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new RMISecurityManager());
		}

		try
		{
			// 2. Identify QuizService and create QuizService stub
			Remote service = Naming.lookup("//127.0.0.1:1099/quiz");
			QuizService quizService = (QuizService) service;

			QuizPlayer qPlayer = new QuizPlayer(quizService);
			qPlayer.launch();

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}
}