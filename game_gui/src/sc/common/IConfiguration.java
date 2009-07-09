package sc.common;

public interface IConfiguration {

	public enum ELanguage {
		DE, EN
	}
	
	ELanguage getLanguage();
	void setLanguage(ELanguage language);
}
