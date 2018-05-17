package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "scoreFragment")
public class ScoreFragment {

  @XStreamAsAttribute
  private final String name;

  private final ScoreAggregation aggregation;

  private final boolean relevantForRanking;

  /**
   * might be needed by XStream
   */
  public ScoreFragment() {
    this.name = null;
    this.aggregation = null;
    this.relevantForRanking = false;
  }

  public ScoreFragment(String name) {
    this(name, ScoreAggregation.SUM);
  }

  public ScoreFragment(String name, ScoreAggregation aggregation) {
    this(name, aggregation, true);
  }

  public ScoreFragment(String name, ScoreAggregation aggregation,
                       boolean relevantForRanking) {
    this.name = name;
    this.aggregation = aggregation;
    this.relevantForRanking = relevantForRanking;
  }

  public String getName() {
    return this.name;
  }

  public ScoreAggregation getAggregation() {
    return this.aggregation;
  }

  public boolean isRelevantForRanking() {
    return this.relevantForRanking;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ScoreFragment) {
      ScoreFragment fragment = (ScoreFragment) o;
      if (this.getName().equals(fragment.getName()) &&
              this.getAggregation().equals(fragment.getAggregation()) &&
              this.isRelevantForRanking() == fragment.isRelevantForRanking()) {
        return true;
      }
    }
    return false;
  }

}
