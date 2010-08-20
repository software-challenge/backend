package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * die vier verschiedenen spielfeldtypen
 * @author tkra
 * 
 */

@XStreamAlias(value = "sit:nodetype")
public enum NodeType {

	HOME1, HOME2, SAVE, GRASS;

}