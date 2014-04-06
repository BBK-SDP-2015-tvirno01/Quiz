
import java.util.Comparator;

public class ScoreComparator<QuizScore> implements Comparator<QuizScore>
{
	/**
	*Comparison based on scores
	*@params QuizScores for comparison
	*@returns positive int if scores are in decsending order, negative otherwise
	*/

	public int compare(QuizScore s1,QuizScore s2)
	{
		if(s1.score>s2.score)
		{
			return 1;
		}else{
			return -1;
		}
	}	
}