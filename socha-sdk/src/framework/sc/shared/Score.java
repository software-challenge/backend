package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@XStreamAlias(value = "score")
public class Score implements Iterable<ScoreValue> {

  @XStreamAsAttribute
  private String displayName;

  @XStreamAsAttribute
  private int numberOfTests;

  @XStreamImplicit(itemFieldName = "values")
  private List<ScoreValue> scoreValues = new ArrayList<>(2);

  public Score(ScoreDefinition scoreDefinition, String displayName) {
    for (ScoreFragment fragment : scoreDefinition) {
      scoreValues.add(new ScoreValue(fragment, new BigDecimal(0)));
    }
    this.displayName = displayName;
    this.numberOfTests = 0;
  }

  @Override
  public Iterator<ScoreValue> iterator() {
    return this.scoreValues.iterator();
  }

  public String getDisplayName() {
    return displayName;
  }

  public int getNumberOfTests() {
    return numberOfTests;
  }

  public ScoreDefinition getScoreDefinition() {
    ScoreDefinition scoreDefinition = new ScoreDefinition();
    for (ScoreValue scoreValue : this) {
      scoreDefinition.add(scoreValue.getFragment());
    }
    return scoreDefinition;
  }

  public List<ScoreValue> getScoreValues() {
    return scoreValues;
  }

  public void setNumberOfTests(int numberOfTests) {
    this.numberOfTests = numberOfTests;
  }

}
