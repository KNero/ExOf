package team.balam.exof.container.console.client.executor;

import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.client.Menu;
import team.balam.exof.environment.TextCrypto;

import java.util.Map;

class EtcExecutor {
	EtcExecutor(CommandExecutor executor) {
		executor.putExecutor(Menu.Execute.ETC_ENCODE_TEXT, this::encodeText);
		executor.putExecutor(Menu.Execute.ETC_DECODE_TEXT, this::decodeText);
	}

	void encodeText(Map<String, String> parameter) {
		String text = parameter.get(Command.Key.TEXT);
		TextCrypto textCrypto = new TextCrypto();

		try {
			String result = textCrypto.encodeBase64(text.getBytes());
			System.out.println("Result text : " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void decodeText(Map<String, String> parameter) {
		String text = parameter.get(Command.Key.TEXT);
		TextCrypto textCrypto = new TextCrypto();

		try {
			byte[] result = textCrypto.decodeBase64(text);
			System.out.println("Result text : " + new String(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
