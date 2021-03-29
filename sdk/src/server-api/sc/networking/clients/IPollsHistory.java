package sc.networking.clients;

public interface IPollsHistory {
  void start();
  void addListener(IHistoryListener listener);
  void removeListener(IHistoryListener listener);
}
