
import java.util.*;
import java.io.*;
import java.text.*;
import java.util.concurrent.atomic.*;
import java.rmi.server.*;
import java.rmi.*;
import java.lang.*;

public class QuizSetUp
{
	private QuizServer qServer;
	public final int playerID;


	/**
	*Constructor method initialises member fields. The player ID is initialised by the login in manager which will either register a new member or verify an existing members credentials
	*@param QuizServer indicates the quiz service being accessed by the quiz player client
	*/
	public QuizSetUp(QuizServer qServer)
	{
		this.qServer = qServer;
		LoginManager LM = new LoginManager(qServer);
		this.playerID = LM.login();
	}

	/**
	*Method launches the application for the quiz set up client. Presents a menu of options available
	*/
	public void launch()
	{
		System.out.println("Welcome to QuizMaster!");

		boolean finished = false;

		try
		{
			while(!finished)
			{
				System.out.println("Hi"+ qServer.getAlias(playerID)+", please select one of the below options:");
				System.out.println("1. Create Quiz");
				System.out.println("2. Close Quiz");
				System.out.println("3. Quit");
	
				int i = Integer.parseInt(System.console().readLine());
	
				if(i==1)
				{
					this.createQuiz();
				}else{
					if(i==2)
					{
						this.closeQuiz();
						}else{
						if(i==3)
						{
						finished = true;
						}else{
							System.out.println("That is not a valid selection");;
						}
					}
				}
			}
		}catch(RemoteException ex){
			ex.printStackTrace();
		}
		System.exit(0);
	}


	/**
	*Method presents menu to the quiz set up client allowing them to create a new quiz to be saved to the server
	*@throws RemoteException
	*@return int Quiz ID of the newly created quiz
	*/
	private int createQuiz()
	{
		int quizID = -1;
		try
		{
			System.out.println("Please enter the name of your quiz:");
	
			String quizName = System.console().readLine();
	
			quizID = qServer.makeNewQuiz(quizName,this.playerID);
			
			boolean finished = false;
			int i = 1;
			String question;		
			ArrayList answerList = null;
			do
			{
				question = addQuestion(i);
	
				if(question.equals(null))
					{
					finished = true;
				}else{
					System.out.println("Please enter the number of possible answers for this question");
						answerList = addAnswers(question,Integer.parseInt(System.console().readLine()));
		
					qServer.addQuestion(quizID,playerID,question,answerList);
					i++;
				}
			}while(!finished);
		}catch(RemoteException ex){
			ex.printStackTrace();
		}
		return quizID;
		
	}

	/**
	*Method to present a menu to the quiz set up client to allow them to add a question to a quiz
	*@param int Question number of the question to be added to the quiz
	*@return String question text entered by the client
	*/
	private String addQuestion(int quNum)
	{
		System.out.println("Would you like to add a quesion? (y/n)");
		
		if(!System.console().readLine().equals("y")&&!System.console().readLine().equals("n"))
		{
			System.out.println("Invalid response");
			return this.addQuestion(quNum);
		}else{
			if(System.console().readLine().equals("y"))
			{
				System.out.println("Please enter question "+quNum);
				return System.console().readLine();
			}
		}
		return null;
	}

	/**
	*Method to present a menu to the client to allow them  to add possible answers for a given question
	*@param String question for which answers are to be provided
	*@param int number of possible answers for the question to be entered
	*@return ArrayList of the possible answers entered by the client
	*/
	private ArrayList<String> addAnswers(String question, int answerListSize)
	{
		System.out.println("Please enter the correct answer for the question: "+System.getProperty("line.separator")+question);
		
		ArrayList<String> answerList = new ArrayList<String>(answerListSize);
		int i = 0;
		answerList.set(i,System.console().readLine());
		answerListSize = answerListSize - 1;
		do
		{
			i++;
			System.out.println("Please enter incorrect answer "+i+" of "+answerListSize);			
			answerList.set(i,System.console().readLine());

		}while(i<=answerListSize);

		return answerList;
		
	}


	private void closeQuiz()
	{
		try
		{
			System.out.println("Please enter the ID of the Quiz you want to close");
			int quizID = Integer.parseInt(System.console().readLine());
			System.out.println(qServer.closeQuiz(quizID,this.playerID));
		}catch(RemoteException ex){
			ex.printStackTrace();
		}
	}


}