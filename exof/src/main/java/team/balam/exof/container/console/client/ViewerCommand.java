package team.balam.exof.container.console.client;

/**
 * Created by kwonsm on 2017. 6. 21..
 * client viewer에서 사용자의 입력을 객체화 시켜준다
 */
class ViewerCommand {
	private String levelOne;
	private String levelTwo;

	boolean setLevelOne(String levelOne) throws TerminateException {
		if (Menu.QUIT.equals(levelOne)) {
			throw new TerminateException();
		} else if (!Menu.LevelOne.GET.equals(levelOne) && !Menu.LevelOne.SET.equals(levelOne)) {
			return false;
		}

		this.levelOne = levelOne;
		return true;
	}

	void setLevelTwo(String levelTwo) throws TerminateException {
		if (Menu.QUIT.equals(levelTwo)) {
			throw new TerminateException();
		}

		this.levelTwo = levelTwo;
	}

	@Override
	public String toString() {
		return this.levelOne + "." + this.levelTwo;
	}
}
