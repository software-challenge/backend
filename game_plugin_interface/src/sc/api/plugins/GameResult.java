package sc.api.plugins;

import java.util.List;

import sc.api.plugins.host.PlayerScore;
import sc.helpers.StringHelper;

public class GameResult
{
	private final ScoreDefinition	definition;
	private final List<PlayerScore>	scores;

	public GameResult(ScoreDefinition definition, List<PlayerScore> scores)
	{
		this.definition = definition;
		this.scores = scores;
	}

	public ScoreDefinition getDefinition()
	{
		return this.definition;
	}

	public List<PlayerScore> getScores()
	{
		return this.scores;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		int playerIndex = 0;
		for (PlayerScore score : this.scores)
		{
			builder.append("--------------------------\n");
			builder.append(" Player " + playerIndex + "\n");
			builder.append("--------------------------\n");
			String[] scoreParts = score.toStrings();
			for (int i = 0; i < scoreParts.length; i++)
			{
				String key = StringHelper.pad(this.definition.get(i).getName(),
						10);
				String value = scoreParts[i];
				builder.append(" " + key + " = " + value + "\n");
			}
			playerIndex++;
		}

		return builder.toString();
	}
}
