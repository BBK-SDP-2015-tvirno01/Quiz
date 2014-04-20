
import java.util.*;
import java.io.*;
import java.text.*;
import java.util.concurrent.atomic.*;
import java.rmi.server.*;
import java.rmi.*;
import java.lang.*;

public class QuizSetUp
{
	private QuizService qServer;
	public final int playerID;

	public QuizSetUp(QuizService qServer)
	{
		this.qServer = qServer;
		LoginManager LM = new LoginManager(qServer);
		this.playerID = LM.login();
	}

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
							throw new IllegalArgumentException();
						}
					}
				}
			}
		}catch(RemoteException ex){
			ex.printStackTrace();
		}
		System.exit(0);
	}

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