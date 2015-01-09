package com.hobby.utilities;

import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Keyboard {
	static Robot robot;
	
	public static void keyPress(String c){
		try {
			robot = new Robot();
			if(c.startsWith("bkspc"))
			{
				robot.keyPress(KeyEvent.VK_BACK_SPACE);
				robot.delay(65);
				robot.keyRelease(KeyEvent.VK_BACK_SPACE);
			} else{
				int keyCode = KeyEvent.getExtendedKeyCodeForChar(c.charAt(0));
				robot.keyPress(keyCode);
				robot.delay(65);
				robot.keyRelease(keyCode);
			}
			
		} catch (Exception e) {

		}
	}
}
