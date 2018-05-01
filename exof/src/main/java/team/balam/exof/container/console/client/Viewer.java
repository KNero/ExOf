package team.balam.exof.container.console.client;

import team.balam.exof.container.console.client.executor.CommandExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Viewer 
{
	private BufferedReader standardReader = null;
	private ViewerCommand command;
	private CommandExecutor commandExecutor = new CommandExecutor();

	public void start() throws Exception {
		System.out.println("   =======             ===");
		System.out.println("   |                  =   =     ==");
		System.out.println("   =======   =   =   =     =   |");
		System.out.println("   |           =      =   =  =====");
		System.out.println("   =======   =   =     ===     |");
		System.out.println();
		System.out.println("+++ Welcome ExOf console monitoring +++");

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
	        System.out.println("\n(1)get info   (2)set info   (3)etc   (9)quit");

        	this.command = new ViewerCommand();
	        if (this.command.setLevelOne(this.standardReader.readLine().trim())) {
		        try {
	        	    this._showLevelTwoMenu();
		        } catch (TerminateException e) {
		        	//종료 예외가 발생할 경우 입력을 다시 받는다.
		        }
	        } else {
		        System.out.println("Enter number 1 or 2 or 9");
	        }
        }
	}

	private void _showLevelTwoMenu() throws IOException {
		while (true) {
			if (Menu.LevelOne.GET.equals(this.command.getLevelOne())) {
				System.out.println("\n(1)service list\n(2)schedule list\n(3)dynamic setting\n(4)port info\n(9)quit");
			} else if (Menu.LevelOne.SET.equals(this.command.getLevelOne())) {
				System.out.println("\n(1)service variable\n(2)scheduler on/off\n(3)scheduler cron expression\n" +
						"(4)add dynamic setting\n(5)update dynamic setting value, description\n(6)remove dynamic setting\n(9)quit");
			} else if (Menu.LevelOne.ETC.equals(this.command.getLevelOne())) {
				System.out.println("(1)encode text\n(2)decode text\n(9)quit");
			} else {
				System.out.println("\nNot supported yes.");
				throw new TerminateException();
			}

			this.command.setLevelTwo(this.standardReader.readLine().trim());

			try {
				CommandParameterReader parameterReader = new CommandParameterReader(this.standardReader, this.command);
				parameterReader.execute();
			} catch (TerminateException e) {
				this.command.clearParameter();
				continue;
			}

			System.out.println("\n----------------------------------------------------------------------------------");
			try {
				this.commandExecutor.execute(this.command);
				this.command.clearParameter();
			} catch (NotFoundOperation e) {
				System.out.println("There is no menu.");
			}
			System.out.println("----------------------------------------------------------------------------------\n");
		}
	}
}
