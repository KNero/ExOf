package team.balam.exof.environment;

import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.db.DynamicSettingDao;
import team.balam.exof.environment.vo.DynamicSettingVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamicSetting {
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicSetting.class);

	private static DynamicSetting self = new DynamicSetting();

	private DynamicSetting() {

	}

	public static DynamicSetting getInstance() {
		return self;
	}

	public void put(DynamicSettingVo vo) throws Exception {
		if (StringUtil.isNullOrEmpty(vo.getName())) {
			throw new IllegalArgumentException("DynamicSetting name is not null.");
		}

		if (StringUtil.isNullOrEmpty(vo.getValue())) {
			throw new IllegalArgumentException("DynamicSetting value is not null.");
		}

		DynamicSettingDao.insert(vo.getName(), vo.getValue(), vo.getDescription());
	}

	/**
	 * parameter 로 받은 name 과 같은 key 값을 가져온다.
	 */
	public DynamicSettingVo get(String name) {
		try {
			Map<String, Object> result = DynamicSettingDao.select(name);
			if (result != null) {
				return new DynamicSettingVo(name, (String) result.get("value"), (String) result.get("description"));
			} else {
				return DynamicSettingVo.EMPTY_VO;
			}
		} catch (Exception e) {
			LOGGER.error("Fail to get value. name[{}]", name, e);
			return new DynamicSettingVo();
		}
	}

	/**
	 * parameter 로 받은 containName 의 값을 포함하고 있는 key 에 해당하는 모든 DynamicSettingVo list 를 반환한다.
	 * @param containName 찾을려는 이름에 포함된 문자
	 */
	public List<DynamicSettingVo> getList(String containName) {
		try {
			List<DynamicSettingVo> list = new ArrayList<>();

			List<Map<String, Object>> result = DynamicSettingDao.selectList(containName);
			for (Map<String, Object> m : result) {
				String name = (String) m.get("name");
				String value = (String) m.get("value");
				String des = (String) m.get("description");

				list.add(new DynamicSettingVo(name, value, des));
			}

			return list;
		} catch (Exception e) {
			LOGGER.error("Fail to get value list. contains name[{}]", containName, e);
			return new ArrayList<>();
		}
	}

	public void change(DynamicSettingVo vo) throws Exception {
		if (StringUtil.isNullOrEmpty(vo.getName())) {
			throw new IllegalArgumentException("DynamicSetting name is not null.");
		}

		if (StringUtil.isNullOrEmpty(vo.getValue())) {
			throw new IllegalArgumentException("DynamicSetting value is not null.");
		}

		String newDes = vo.getDescription();
		if (newDes == null || newDes.isEmpty()) {
			DynamicSettingVo oldVo = DynamicSetting.getInstance().get(vo.getName());
			vo = new DynamicSettingVo(vo.getName(), vo.getValue(), oldVo.getDescription());
		}

		DynamicSettingDao.update(vo.getName(), vo.getValue(), vo.getDescription());
	}

	public void remove(String name) throws Exception {
		if (StringUtil.isNullOrEmpty(name)) {
			throw new IllegalArgumentException("DynamicSetting name is not null.");
		}

		DynamicSettingDao.delete(name);
	}
}
