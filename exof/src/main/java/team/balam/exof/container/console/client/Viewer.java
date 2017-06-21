package team.balam.exof.container.console.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Viewer 
{
	public BufferedReader standardReader = null;
	private String selectMenuNumber;
	private ViewerCommand command;

	public void start() throws Exception {
		System.out.println("   =======             ===");
		System.out.println("   |                  =   =     ==");
		System.out.println("   =======   =   =   =     =   |");
		System.out.println("   |           =      =   =  =====");
		System.out.println("   =======   =   =     ===     |");
		System.out.println();
		System.out.println("+++ Welcom ExOf console monitoring +++");

		this.standardReader = new BufferedReader(new InputStreamReader(System.in));

		try {
			while (true) {
				this._showLevelOneMenu();
			}
		} catch (TerminateException e) {
			//종료 예외는 무시
		} finally {
			if (this.standardReader != null) {
				this.standardReader.close();
			}

			System.out.println("+++ Bye Bye +++");
		}
	}

	private void _showLevelOneMenu() throws IOException {
        while (true) {
	        System.out.println("\n(1)get info   (2)set info   (3)quit");

        	this.command = new ViewerCommand();
	        if (this.command.setLevelOne(this.standardReader.readLine())) {
		        try {
	        	    this._showLevelTwoMenu();
		        } catch (TerminateException e) {
		        	//종료 예외가 발생할 경우 입력을 다시 받는다.
		        }
	        } else {
		        System.out.println("Enter numbaer 1 or 2 or 3");
	        }
        }
	}

	private void _showLevelTwoMenu() throws IOException {
		while (true) {
			System.out.println("\n(1)service list   (2)schedule list   (3)quit");

			this.command.setLevelTwo(this.standardReader.readLine());

			try {
				Executor.execute(this.command);
			} catch (NotFoundOperation e) {
				System.out.println("There is no menu. (Enter numbaer 1 ~ 3)");
			}
		}
	}
}
