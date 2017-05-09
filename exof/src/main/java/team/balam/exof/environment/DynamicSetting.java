package team.balam.exof.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.internal.StringUtil;
import team.balam.exof.db.DynamicSettingDao;

public class DynamicSetting {
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicSetting.class);
	private static DynamicSetting self = new DynamicSetting();
	
	private DynamicSetting() {
		try {
			DynamicSettingDao.createTable();
		} catch(Exception e) {
			LOGGER.error("Fail to create env table", e);
		}
	}
	
	public static DynamicSetting getInstance() {
		return self;
	}
	
	public void put(DynamicSettingVo _vo) {
		try {
			if (StringUtil.isNullOrEmpty(_vo.getName())) {
				throw new Exception("DynamicSetting name is not null.");
			}
			
			if (StringUtil.isNullOrEmpty(_vo.getValue())) {
				throw new Exception("DynamicSetting value is not null.");
			}
			
			DynamicSettingDao.insert(_vo.getName(), _vo.getValue(), _vo.getDescription());
		} catch(Exception e) {
			LOGGER.error("Fail to put dynamic setting. {}/{}/{}", _vo.getName(), _vo.getValue(), _vo.getDescription(), e);
		}
	}
	
	public DynamicSettingVo get(String _name) {
		try {
			Map<String, Object> result = DynamicSettingDao.select(_name);
			
			DynamicSettingVo vo = new DynamicSettingVo();
			vo.setName(_name);
			vo.setValue((String)result.get("value"));
			vo.setDescription((String)result.get("description"));
			
			return vo;
		} catch(Exception e) {
			LOGGER.error("Fail to get value. name[{}]", _name, e);
			return new DynamicSettingVo();
		}
	}
	
	public List<DynamicSettingVo> getList(String _containName) {
		try {
			List<DynamicSettingVo> list = new ArrayList<>();
			
			List<HashMap<String, Object>> result = DynamicSettingDao.selectList(_containName);
			for (HashMap<String, Object> m : result) {
				DynamicSettingVo vo = new DynamicSettingVo();
				vo.setName((String)m.get("name"));
				vo.setValue((String)m.get("value"));
				vo.setDescription((String)m.get("description"));
				list.add(vo);
			}
			
			return list;
		} catch(Exception e) {
			LOGGER.error("Fail to get value list. contains name[{}]", _containName, e);
			return new ArrayList<>();
		}
	}
	
	public void change(DynamicSettingVo _vo) {
		try {
			if (StringUtil.isNullOrEmpty(_vo.getName())) {
				throw new Exception("DynamicSetting name is not null.");
			}
			
			if (StringUtil.isNullOrEmpty(_vo.getValue())) {
				throw new Exception("DynamicSetting value is not null.");
			}
			
			if (_vo.getDescription() == null) {
				_vo.setDescription("");
			}
			
			DynamicSettingDao.update(_vo.getName(), _vo.getValue(), _vo.getDescription());
		} catch(Exception e) {
			LOGGER.error("Fail to change dynamic setting. {}/{}/{}", _vo.getName(), _vo.getValue(), _vo.getDescription(), e);
		}
	}
}
