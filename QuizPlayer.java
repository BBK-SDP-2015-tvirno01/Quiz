

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

	/**
	*Constructor method initialises member fields. The player ID is initialised by the login in manager which will either register a new member or verify an existing members credentials
	*@param QuizServer indicates the quiz service being accessed by the quiz player client
	*/
	public QuizPlayer(QuizServer qServer)
	{
		this.qServer = qServer;
		LoginManager LM = new LoginManager(qServer);
		this.playerID = LM.login();
	}

	/**
	*Method launches the application for the quiz player client. Presents a menu of options available to the quiz player
	*/
	public void launch()
	{
		System.out.println("Welcome to QuizMaster!");
		int quizID;
		boolean finished = false;
		try
		{
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
						System.out.println("Returning to main menu...");
					}else{
						this.playQuiz(quizID);
					}
				}else{
					if(i==2)
					{
						finished = true;
					}else{
						System.out.println("That is not a valid option");
					}
				}
			}
		}catch(RemoteException ex){
			ex.printStackTrace();
		}
		System.exit(0);
	}

	/**
	*Method to present menu to quiz player client to select a quiz to play.
	*The player can either search for a quiz using a keyword search or can play a random quiz.
	*@throws RemoteException
	*@throws IllegalArgumentExcpetion if the player does not select a possible menu option
	*@return int Quiz ID of the selected quiz. Returns -1 if an exception is thrown.
	*/
	private int selectQuiz() 
	{
		int quizID = -1;

		System.out.println("Please select one of the below options");
		System.out.println("1. Search for a quiz");
		System.out.println("2. Play a random quiz");
		System.out.println("3. Return to main menu");

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
		}catch(IllegalArgumentException ex){
			System.out.println("That is not a valid selection. Returning to main menu");
		}

		return quizID;
	}

	/**
	*Method to present menu to player to search for a quiz using the keyword search option
	*@returns int Quiz ID of the search result chosen by quiz player
	*/
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

	/**
	*Method to present the quiz player with a multiple choice menu. This method contiues to call recursively if the user fails to select a valid option
	*@param ArrayList of possible options
	*@return String text of the selected option
	*/
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

	/**
	*Method to present menu to player allowing them to play the given quiz
	*@throws RemoteException
	*@param int Quiz ID of the quiz to be played
	*
	*/	
	private void playQuiz(int quizID)
	{
		try
		{
			if(!qServer.doesQuizExist(quizID))
			{
				throw new IllegalArgumentException();
			}else{
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
			}
		}catch(RemoteException ex){
			ex.printStackTrace();
		}catch(IllegalArgumentException ex){
			System.out.println("I'm afraid the selected quiz does not exist or has been closed. Returning to main menu");
		}

	}
} 

