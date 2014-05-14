package sc.plugin2015;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ GameStateTest.class, WelcomeMessageTest.class,
        WinnerAndReasonTest.class, sc.plugin2015.converters.AllTests.class,
        sc.plugin2015.entities.AllTests.class,
        sc.plugin2015.exceptions.AllTests.class,
        sc.plugin2015.moves.AllTests.class, sc.plugin2015.laylogic.AllTests.class })
public class AllTests {

}
