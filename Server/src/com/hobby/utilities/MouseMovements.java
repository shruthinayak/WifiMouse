package com.hobby.utilities;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class MouseMovements {
	static Robot robot;

	public static void mouseMove(int x, int y, int sensitivity) {
		PointerInfo ptr = MouseInfo.getPointerInfo();
		int curX = ptr.getLocation().x;
		int curY = ptr.getLocation().y;
		try {
			robot = new Robot();
			if (sensitivity != 0) {
				x = x * sensitivity;
				y = y * sensitivity;
			}
			System.out.println("onScreen: " + (curX + x) + "," + (curY + y));
			robot.mouseMove(curX + x, curY + y);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void mouseLeftClick() {
		try {
			robot = new Robot();
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			sleep(65);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		} catch (Exception e) {

		}
	}

	public static void mouseRightClick() {
		try {
			robot = new Robot();
			robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
			sleep(65);
			robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		} catch (Exception e) {

		}
	}
	
	public static void mouseDoubleClick(){
		try {
			robot = new Robot();
			robot.mousePress(InputEvent.BUTTON1_MASK);
	        sleep(50);
	        robot.mouseRelease(InputEvent.BUTTON1_MASK);
	        robot.mousePress(InputEvent.BUTTON1_MASK);
	        sleep(50);
	        robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (Exception e) {

		}
		 
	}

	public static void mouseScroll(boolean up){
		try{
			robot = new Robot();
			if(up){
				robot.mouseWheel(-100);
			} else {
				robot.mouseWheel(100);
			}
		} catch(Exception e){
			
		}
	}
	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
