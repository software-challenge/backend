package sc.plugin_minimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * die vier verschiedenen spielfeldtypen
 * @author tkra
 * 
 */

@XStreamAlias(value = "minimal:nodetype")
public enum NodeType {

	HOME1, HOME2, FENCE, GRASS;

}