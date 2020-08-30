package config.tutorial;

public class MockMethod {
	private String methodName;
	private String parameterTypes;
	private String returnObject;
	private Mock mock;
	
	MockMethod(String methodName, String parameterTypes, String returnObject, Mock mock) {
		super();
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.returnObject = returnObject;
		this.mock = mock;
	}
}
