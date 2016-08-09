package sc.plugin2017.util;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class BoardConverter implements Converter{

  @Override
  public boolean canConvert(Class arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void marshal(Object arg0, HierarchicalStreamWriter arg1,
      MarshallingContext arg2) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader arg0,
      UnmarshallingContext arg1) {
    // TODO Auto-generated method stub
    return null;
  }

}
