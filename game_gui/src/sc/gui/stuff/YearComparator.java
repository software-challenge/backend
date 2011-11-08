package sc.gui.stuff;

import java.util.Comparator;

import sc.plugin.GUIPluginInstance;

public class YearComparator implements Comparator<GUIPluginInstance> {

	@Override
	public int compare(GUIPluginInstance o1, GUIPluginInstance o2) {
		Integer year1 = new Integer(o1.getPlugin().getPluginYear());
		Integer year2 = new Integer(o1.getPlugin().getPluginYear());
		return year1.compareTo(year2);
	}

}
