
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
		
		this.playerID = login();
		
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
				this.playQuiz();
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

	private int login()
	{
		String strA = "";
		String strB = "";
		char[] pWord;
		Boolean memberVerified = false;
		int result = -1;

		System.out.println("Register or proceed to login");
		
		System.out.println("1. Register");

		System.out.println("2. Login");

		if(Integer.parseInt(System.console().readLine())==1)
		{
			
			System.out.println("Please enter your email address");
			strA = System.console().readLine();
			System.out.println("Please enter your password");
			pWord = System.console().readLine().toCharArray();
			System.out.println("Please enter your Quiz Master alias");
			strB = System.console().readLine();

			result = qServer.createMember(strB,strA,pWord);
			
		}

		while(!memberVerified)
		{
			System.out.println("Please enter your email address and password");
			strA = System.console().readLine();
			System.out.println("Password");
			strB = System.console().readLine();
			
			result = qServer.authenticateMember(strA,strB.toCharArray());

			memberVerified = result>=0;
		}
		
		return result;
	}

	private void playQuiz()
	{
		int quizID;

		System.out.println("Please select one of the below options");
		System.out.println("1. Search for a quiz");
		System.out.println("2. Play a random quiz");

			int i = Interger.parseInt(System.console().readLine());
			if(i==1)
			{
				quizID = quizSelect();
			}else{
				if(i==2)
				{
					quizID = qServer.getRandomQuizID;
				}else{
					throw IllegalArgumentException;
				}
			}

		qServer.startQuiz(quizID);
	}

	private int quizSelect();
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

		return listMenu(searchResult);
	}

	private int listMenu(Collection c<String>)
	{
		int i = 1;
		for(String str:c)
		{
			System.out.println(i+". "+str);
			i++;
		}

	}








} 