package sc.player2018;

import sc.plugin2018.IGameHandler;

@FunctionalInterface
public interface LogicBuilder {
  IGameHandler build(Starter client);
}
