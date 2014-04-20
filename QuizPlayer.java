

import java.util.*;
import java.io.*;
import java.text.*;
import java.util.concurrent.atomic.*;
import java.rmi.server.*;
import java.rmi.*;
import java.lang.*;

public class QuizPlayer
{
	private QuizServer qServer;
	public final int playerID;

	public QuizPlayer(QuizServer qServer)
	{
		this.qServer = qServer;
		LoginManager LM = new LoginManager(qServer);
		this.playerID = LM.login();
	}

	public void launch()
	{
		System.out.println("Welcome to QuizMaster!");
		int quizID;
		boolean finished = false;

		while(!finished)
		{
			System.out.println("Hi"+ qServer.getAlias(playerID)+", please select one of the below options:");
			System.out.println("1. Play Quiz");
			System.out.println("2. Quit");
			//more options e.g. player history/achievements, other players playing, challenge other player...
			
			int i = Integer.parseInt(System.console().readLine());
			if(i==1)
			{
				quizID = this.selectQuiz();
				if(quizID==-1)
				{
					finished = true;
				}else{
					this.playQuiz(quizID);
				}
			}else{
				if(i==2)
				{
					finished = true;
				}else{
					throw new IllegalArgumentException();
				}
			}
		}

		System.exit(0);
	}


	private int selectQuiz() 
	{
		int quizID = 0;

		System.out.println("Please select one of the below options");
		System.out.println("1. Search for a quiz");
		System.out.println("2. Play a random quiz");
		System.out.println("3. Quit");

		try
		{

			int i = Integer.parseInt(System.console().readLine());
			if(i==1)
			{
				quizID = quizSearch();
			}else{
				if(i==3)
				{
					quizID = -1;
				}else{
					if(i==2)
					{
						quizID = qServer.getRandomQuizID();
					}else{
						throw new IllegalArgumentException();
					}
				}
			}
		}catch(RemoteException ex){
			ex.printStackTrace();
		}

		return quizID;
	}

	private int quizSearch()
	{
		boolean searchFinished = false;
		ArrayList<String> searchResult = null;
		int result = -1;
		try
		{
			while(!searchFinished)
			{
				System.out.println("Please enter a search keyword:");
				searchResult = qServer.searchQuiz(System.console().readLine());
		
				if(searchResult.equals(null))
				{
					System.out.println("No Search results.");
					searchFinished = false;
				}else{
					searchFinished = true;
				}
			}

			result = qServer.getQuizID(listMenuSelection(searchResult));
		}catch(RemoteException ex){
			ex.printStackTrace();
		}
		return result;
	}

	private String listMenuSelection(ArrayList<String> strL)
	{	
		String result = null;

		while(result.equals(null))
		{
			System.out.println("Please select one of the below:");
	
			int i = 1;
			for(String str:strL)
			{
				System.out.println(i+". "+str);
				i++;
			}
	
			int selection = Integer.parseInt(System.console().readLine());
	
			if(selection > i || selection < 1)
			{
				System.out.println("That is not a valid answer");
				result = listMenuSelection(strL);
			}else{
				result = strL.get(selection-1);
			}
		}		
		
		return result;

	}

	
	private void playQuiz(int quizID)
	{
		try
		{
			int i = 1;
			int numberOfQuestions = qServer.getNumberOfQuestions(quizID);
			int score = 0;
	
			System.out.println("Get ready to play "+qServer.getQuizName(quizID)+"!");
			
			while(i<=numberOfQuestions)
			{
				System.out.println(i+". "+qServer.getQuestion(quizID,i));
				
					score = score + qServer.checkAnswer(quizID,i,listMenuSelection(qServer.getAnswerSet(quizID,i)));
	
				i++;
			}
	
		qServer.submitScore(quizID,playerID,score);

		System.out.println("Congratulations, you scored "+score+" out of "+numberOfQuestions+"!");

		}catch(RemoteException ex){
			ex.printStackTrace();
		}

	}
} 

