package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * die vier verschiedenen spielfeldtypen, HOME1 sind die heimatfelder des
 * ersten/roten spielers. HOME2 sind die heimatfelder des zweiten/blauen
 * spielers. SAVE sind sicherheitsfelder unf GRASS normale felder.
 * 
 * @author tkra
 * 
 */

@XStreamAlias(value = "sit:nodetype")
public enum NodeType {

	HOME1, HOME2, SAVE, GRASS;

}