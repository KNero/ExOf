package team.balam.exof.environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.internal.StringUtil;
import team.balam.exof.db.DynamicSettingDao;
import team.balam.exof.environment.vo.DynamicSettingVo;

public class DynamicSetting {
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicSetting.class);

	private Map<String, DynamicSettingVo> cash = new ConcurrentHashMap<>();
	private static DynamicSetting self = new DynamicSetting();

	private DynamicSetting() {

	}

	public static DynamicSetting getInstance() {
		return self;
	}

	public void put(DynamicSettingVo _vo) throws Exception {
		if (StringUtil.isNullOrEmpty(_vo.getName())) {
			throw new IllegalArgumentException("DynamicSetting name is not null.");
		}

		if (StringUtil.isNullOrEmpty(_vo.getValue())) {
			throw new IllegalArgumentException("DynamicSetting value is not null.");
		}

		this.cash.put(_vo.getName(), _vo);
		DynamicSettingDao.insert(_vo.getName(), _vo.getValue(), _vo.getDescription());
	}

	/**
	 * parameter로 받은 name과 같은 key 값을 가져온다.
	 * cash에 값이 있다면 그 값을 바로 리턴하고 없다면 db에서 로딩한다
	 * @param _name
	 * @return
	 */
	public DynamicSettingVo get(String _name) {
		try {
			DynamicSettingVo vo = this.cash.get(_name);
			if (vo == null) {
				Map<String, Object> result = DynamicSettingDao.select(_name);
				if (result != null) {
					vo = new DynamicSettingVo(_name, (String) result.get("value"), (String) result.get("description"));
					this.cash.put(_name, vo);

					return vo;
				} else {
					return DynamicSettingVo.EMPTY_VO;
				}
			}

			return vo;
		} catch (Exception e) {
			LOGGER.error("Fail to get value. name[{}]", _name, e);
			return new DynamicSettingVo();
		}
	}

	/**
	 * parameter로 받은 containName의 값을 포함하고 있는 key에 해당하는 모든 DynamicSettingVo list를 반환한다.
	 * list는 무조건 DB에서 로딩해서 가져온다.
	 * @param _containName
	 * @return
	 */
	public List<DynamicSettingVo> getList(String _containName) {
		try {
			List<DynamicSettingVo> list = new ArrayList<>();

			List<Map<String, Object>> result = DynamicSettingDao.selectList(_containName);
			for (Map<String, Object> m : result) {
				String name = (String) m.get("name");
				String value = (String) m.get("value");
				String des = (String) m.get("description");

				DynamicSettingVo vo = new DynamicSettingVo(name, value, des);
				this.cash.put(name, vo);
				list.add(vo);
			}

			return list;
		} catch (Exception e) {
			LOGGER.error("Fail to get value list. contains name[{}]", _containName, e);
			return new ArrayList<>();
		}
	}

	public void change(DynamicSettingVo _vo) throws Exception {
		if (StringUtil.isNullOrEmpty(_vo.getName())) {
			throw new IllegalArgumentException("DynamicSetting name is not null.");
		}

		if (StringUtil.isNullOrEmpty(_vo.getValue())) {
			throw new IllegalArgumentException("DynamicSetting value is not null.");
		}

		String newDes = _vo.getDescription();
		if (newDes == null || newDes.isEmpty()) {
			DynamicSettingVo oldVo = DynamicSetting.getInstance().get(_vo.getName());
			_vo = new DynamicSettingVo(_vo.getName(), _vo.getValue(), oldVo.getDescription());
		}

		DynamicSettingDao.update(_vo.getName(), _vo.getValue(), _vo.getDescription());
		this.cash.put(_vo.getName(), _vo);
	}

	public void remove(String _name) throws Exception {
		if (StringUtil.isNullOrEmpty(_name)) {
			throw new IllegalArgumentException("DynamicSetting name is not null.");
		}

		DynamicSettingDao.delete(_name);
		this.cash.remove(_name);
	}
}
