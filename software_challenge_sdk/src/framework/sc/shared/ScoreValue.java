package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.math.BigDecimal;

@XStreamAlias(value = "scoreValue")
public class ScoreValue {

  private ScoreFragment fragment;

  private BigDecimal value;

  public ScoreValue(ScoreFragment fragment, BigDecimal value) {
    this.fragment = fragment;
    this.value = value;
  }

  public ScoreFragment getFragment() {
    return fragment;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }
}
