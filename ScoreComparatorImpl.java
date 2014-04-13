

import java.util.*;
import java.util.Comparator;

public class ScoreComparatorImpl<Q extends QuizScore> implements ScoreComparator<Q>
{
	/**
	*Comparison based on scores
	*@params QuizScores for comparison
	*@returns positive int if scores are in decsending order, negative otherwise
	*/

	public int compare(Q QS1,Q QS2)
	{	
		int s1 = QS1.getScore();
		int s2 = QS2.getScore();

		if(s1>s2)
		{
			return 1;
		}else{
			return -1;
		}
	}	
}