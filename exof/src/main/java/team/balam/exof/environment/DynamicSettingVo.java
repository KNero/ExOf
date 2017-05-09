package team.balam.exof.environment;

public class DynamicSettingVo {
	private String name;
	private String value;
	private String description;
	
	public DynamicSettingVo() {
		
	}
	
	public DynamicSettingVo(String _name, String _value) {
		this.name = _name;
		this.value = _value;
	}
	
	public DynamicSettingVo(String _name, String _value, String _description) {
		this.name = _name;
		this.value = _value;
		this.description = _description;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
