
import java.util.*;
import java.io.*;

public class QuizScore implements Serializable
{
	public final int memberID;
	public final int score;

	public QuizScore(int memberID, int score)
	{
		this.memberID = memberID;
		this.score = score;
	}

	public int getScore()
	{
		return this.score;
	}
}