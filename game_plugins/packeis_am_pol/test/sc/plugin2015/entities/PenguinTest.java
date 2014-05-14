package sc.plugin2015.entities;

import static org.junit.Assert.*;

import org.junit.Test;

import sc.plugin2015.Penguin;
import sc.plugin2015.PlayerColor;

public class PenguinTest {

	
	@Test
	public void testPenguin() {
		Penguin p1 = new Penguin(PlayerColor.RED);
		Penguin p2;
		try {
			p2 =(Penguin) p1.clone();
			assertTrue(p2.equals(p1));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNullPenguin() {
		Penguin p1 = new Penguin();
		Penguin p2;
		try {
			p2 =(Penguin) p1.clone();
			assertTrue(p2.equals(p1));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

}
