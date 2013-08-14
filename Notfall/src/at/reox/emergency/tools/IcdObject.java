package at.reox.emergency.tools;

public class IcdObject {

    private final String code;
    private final String name;

    public IcdObject(String code, String name) {
	this.code = code;
	this.name = name;
    }

    public String getCode() {
	return code;
    }

    public String getName() {
	return name;
    }

    @Override
    public String toString() {
	return name;
    }

}
