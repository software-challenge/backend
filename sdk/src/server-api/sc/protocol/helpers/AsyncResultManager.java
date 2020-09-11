package sc.protocol.helpers;

import sc.api.plugins.host.IRequestResult;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.ProtocolMessage;

import java.util.*;

public class AsyncResultManager {
  private Map<Class<? extends ProtocolMessage>, Collection<IRequestResult>> handlers = new HashMap<>();
  
  public void invokeHandlers(ProtocolMessage response) {
    Collection<IRequestResult> responseHandlers = getHandlers(response.getClass());
    this.handlers.remove(response.getClass());
    
    for (IRequestResult handler : responseHandlers) {
      if (response instanceof ProtocolErrorMessage) {
        handler.handleError((ProtocolErrorMessage) response);
      } else {
        handler.operate(response);
      }
    }
  }
  
  private Collection<IRequestResult> getHandlers(Class<? extends ProtocolMessage> responseClass) {
    Collection<IRequestResult> current = this.handlers.get(responseClass);
    Collection<IRequestResult> result = new ArrayDeque<>();
    
    if (current != null)
      result.addAll(current);
    
    return result;
  }
  
  public void addHandler(Class<? extends ProtocolMessage> response, IRequestResult handler) {
    Collection<IRequestResult> current = this.handlers.computeIfAbsent(response, k -> new ArrayDeque<>());
    current.add(handler);
  }
  
}
