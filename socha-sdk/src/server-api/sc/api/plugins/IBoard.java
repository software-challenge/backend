package sc.api.plugins;

public interface IBoard extends Cloneable {
  IField getField(int x, int y);
  //IField getField(Coord c); Wie lösen?
  IField getField(int x, int y, int z);
}
