package sc.plugin2015.moves;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DebugHintTest.class, ExchangeMoveTest.class, LayMoveTest.class,
        MoveTest.class })
public class AllTests {

}
