package sc.api.plugins;

public interface IBoard extends Cloneable {
  IField getField(int x, int y);
}
