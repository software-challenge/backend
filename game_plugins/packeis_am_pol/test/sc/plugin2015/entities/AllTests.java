package sc.plugin2015.entities;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BoardTest.class, FieldTest.class, PlayerColorTest.class,
        PlayerTest.class, StoneBagTest.class, StoneColorTest.class,
        StoneShapeTest.class, PenguinTest.class })
public class AllTests {

}
