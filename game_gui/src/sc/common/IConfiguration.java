package sc.common;

import java.io.Serializable;

public interface IConfiguration extends Serializable {

	public enum ELanguage {
		DE, EN
	}
	
	ELanguage getLanguage();
	void setLanguage(ELanguage language);
}
