package team.balam.exof.container.console.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwonsm on 2017. 6. 21..
 * client viewer 에서 사용자의 입력을 객체화 시켜준다
 */
class ViewerCommand {
	private String levelOne;
	private String levelTwo;

	private Map<String, Object> parameter = new HashMap<>();

	boolean setLevelOne(String levelOne) throws TerminateException {
		if (Menu.QUIT.equals(levelOne)) {
			throw new TerminateException();
		} else if (!Menu.LevelOne.GET.equals(levelOne) && !Menu.LevelOne.SET.equals(levelOne)) {
			return false;
		}

		this.levelOne = levelOne;
		return true;
	}

	public String getLevelOne() {
		return levelOne;
	}

	void setLevelTwo(String levelTwo) throws TerminateException {
		if (Menu.QUIT.equals(levelTwo)) {
			throw new TerminateException();
		}

		this.levelTwo = levelTwo;
	}

	void putParameter(String _key, Object _value) {
		this.parameter.put(_key, _value);
	}

	public Map<String, Object> getParameter() {
		return parameter;
	}

	@Override
	public final String toString() {
		return this.levelOne + "." + this.levelTwo;
	}
}
