package sc.plugin2014;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ GameStateTest.class, WelcomeMessageTest.class,
        WinnerAndReasonTest.class, sc.plugin2014.converters.AllTests.class,
        sc.plugin2014.entities.AllTests.class,
        sc.plugin2014.exceptions.AllTests.class,
        sc.plugin2014.moves.AllTests.class, sc.plugin2014.laylogic.AllTests.class })
public class AllTests {

}
