package sc.plugin2014.converters;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ExchangeMoveConverterTest.class, GameStateConverterTest.class,
        LayMoveConverterTest.class })
public class AllTests {

}
