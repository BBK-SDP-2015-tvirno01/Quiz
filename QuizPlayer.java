
public class QuizPlayer
{
	private QuizService qServer;
	public final int playerID;

	public QuizPlayer(QuizService qServer)
	{
		this.qServer = qServer;
	}

	public void launch()
	{
		System.out.println("Welcome to QuizMaster!");
		
		LoginManager LM = new LoginManager(qServer);

		this.playerID = LM.login();
		
		boolean finished = false;

		while(!finished)
		{
			System.out.println("Hi"+ qServer.getMemberAlias(playerID)+", please select one of the below options:");
			System.out.println("1. Play Quiz");
			System.out.println("2. Quit");
			//more options e.g. player history/achievements, other players playing, challenge other player...
			
			int i = Interger.parseInt(System.console().readLine());
			if(i==1)
			{
				this.playQuiz(this.selectQuiz());
			}else{
				if(i==2)
				{
					finished = true;
				}else{
					throw IllegalArgumentException;
				}
			}
		}

		System.exit();
	}


	private void selectQuiz()
	{
		int quizID;

		System.out.println("Please select one of the below options");
		System.out.println("1. Search for a quiz");
		System.out.println("2. Play a random quiz");
		System.out.println("3. Quit");

			int i = Interger.parseInt(System.console().readLine());
			if(i==1)
			{
				quizID = quizSearch();
			}else{
				if(i==3)
				{
					System.exit();
				}else{
					if(i==2)
					{
						quizID = qServer.getRandomQuizID;
					}else{
						throw IllegalArgumentException;
					}
				}
			}

		qServer.startQuiz(quizID);
	}

	private int quizSearch();
	{
		boolean searchFinished = false;

		while(!searchFinished)
		{
			System.out.println("Please enter a search keyword:");
			ArrayList<String> searchResult = qServer.searchQuiz(System.console().readLine());
	
			if(searchResult.equals(null))
			{
				System.out.println("No Search results.");
				searchFinished = false;
			}else{
				searchFinished = true;
			}
		}

		return qServer.getQuizID(listMenuSelection(searchResult));
	}

	private String listMenuSelection(List<String> strL)
	{	
		String result;

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
				result = get(selection-1);
			}
		}		
		
		return result;

	}

	
	private void playQuiz(int quizID)
	{
		int i = 1;
		int numberOfQuestions = qServer.getNumberOfQuestions(quizID);
		int score = 0;

		System.out.println("Get ready to play "+qServer.getQuizName(quizID)+"!");
		
		while(i<=numberOfQuestions)
		{
			System.out.println(i+". "+qServer.getQuestion(quizID,this.playerID,i));
			
			score = score + qServer.checkAnswer(quizID,i,listMenuSelection(qServer.getAnswerSet(quizID,i)));

			i++;
		}
		
		qServer.submitScore(quizID,playerID,score);

		System.out.println("Congratulations, you scored "+score+" out of "+numberOfQuestions+"!");
	}
} 

