
public class Quiz
{
	public final int quID;
	public final String quName;
	public final int creatorID;
	public final HashSet<Question> quSet;
	public final ArrayList<QuizScore> quLeaderBoard;
	

	public Quiz(int ID, String name,int creator, HashSet qus)
	{
		this.quName = name;
		this.quSet = qus;	
	}

	public void addQuestion(Question qu)
	{
		this.quSet.add(qu);
	}

	public boolean removeQuestion(Question qu)
	{
		return this.quSet.remove(qu);
	}

	public void addScore(QuizScore newScore)
	{
		this.quLeaderBoard.add(newScore);
	}

	public ArrayList<QuizScore> getLeaderBoard()
	{
		ScoreComparator<QuizScore> cScore = new ScoreComparator<QuizScore>();
		Collections.sort(this.quLeaderBoard,cScore);
		return this.quLeaderBoard;
	}

	public QuizScore getTopScore()
	{
		ScoreComparator<QuizScore> cScore = new ScoreComparator<QuizScore>();
		Collections.sort(this.quLeaderBoard,cScore);
		return this.quLeaderBoard.get(1);
	}
}