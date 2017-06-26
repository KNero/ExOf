package team.balam.exof.environment;

public class DynamicSettingVo {
	private String name;
	private String value;
	private String description;

	public static final DynamicSettingVo EMPTY_VO = new DynamicSettingVo(){
		@Override
		public boolean isValid() {
			return false;
		}
	};

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

	public boolean isValid() {
		return true;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}
}
