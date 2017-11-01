package sc.shared;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias(value = "scoreDefinition")
public class ScoreDefinition implements Iterable<ScoreFragment> {
  @XStreamImplicit(itemFieldName = "fragment")
  private List<ScoreFragment> fragments = new LinkedList<ScoreFragment>();

  public void add(String name) {
    this.fragments.add(new ScoreFragment(name));
  }

  public void add(ScoreFragment fragment) {
    this.fragments.add(fragment);
  }

  public int size() {
    return this.fragments.size();
  }

  public boolean isValid() {
    return size() > 0;
  }

  public ScoreFragment get(int i) {
    return this.fragments.get(i);
  }

  @Override
  public Iterator<ScoreFragment> iterator() {
    return this.fragments.iterator();
  }

  @Override
  public boolean equals(Object o) {
	  if (o instanceof ScoreDefinition) {
	    int i = 0;
      for (ScoreFragment fragment : (ScoreDefinition) o) {
        if (!this.fragments.get(i).equals(fragment)) {
          return false;
        }
      }
    } else {
	    return false;
    }
    return true;
  }
}
