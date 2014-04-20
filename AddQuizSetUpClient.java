

import java.rmi.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.text.*;
import java.util.concurrent.atomic.*;
import java.rmi.server.*;

public class AddQuizSetUpClient
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
			QuizServer quizService = (QuizServer) service;

			QuizSetUp qSetUp = new QuizSetUp(quizService);
			qSetUp.launch();

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}catch(NotBoundException ex){
			ex.printStackTrace();
		}
		
	}
}