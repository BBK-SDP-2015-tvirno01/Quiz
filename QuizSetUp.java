
public class QuizSetUp
{
	private QuizService qServer;
	public final int memberID;

	public QuizSetUp(QuizService qServer)
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
			System.out.println("1. Create Quiz");
			System.out.println("2. Close Quiz");
			System.out.println("3. Quit");
			//additional options e.g. view quizzes, edit quiz, view leaderboards, view quiz stats etc...

			int i = Interger.parseInt(System.console().readLine());

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
						throw IllegalArgumentException;
					}
				}
			}
		}

		System.exit();
	}

	private int createQuiz()
	{
		System.out.println("Please enter the name of your quiz:");

		String quizName = System.console().readine();

		int quizID = qServer.makeNewQuiz(quizName,this.playerID);
		
		boolean finished = false;
		int i = 1;
		String question;		
		List answerList = null;
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

		return quizID;
		
	}

	private String addQuestion(int quNum)
	{
		System.out.println("Would you like to add a quesion? (y/n)");
		
		if(!System.console().readLine.equals("y")&&!System.console().readLine.equals("n"))
		{
			System.out.println("Invalid response");
			return this.addQuestion(quNum);
		}else{
			if(System.console().readLine.equals("y"))
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
		
		List<String> answerList = new ArrayList<String>(answerListSize);
		int i = 0;
		answerList[i] = System.console().readLine();
		
		do
		{
			i++;
			System.out.readLine("Please enter incorrect answer "+i+" of "+answerListSize-1);			
			answerList[i] = System.console().readLine();

		}while(i<answerListSize);

		return answerList;
		
	}


	private void closeQuiz()
	{
		System.out.println("Please enter the ID of the Quiz you want to close");

		System.out.println(qServer.closeQuiz(quizID,playerID));
	}


}