package com.hobby.server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import com.hobby.utilities.Keyboard;
import com.hobby.utilities.MouseMovements;

public class ServerClass {
	static Robot robot;
	static String leftClickCode = "ltclk";
	static String rightClickCode = "rtclk";
	static String doubleClickCode = "dblclk";
	static String scrollUpCode = "up";
	static String scrollDownCode = "down";
	static JFrame frame = new JFrame();
	static Image qr;

	public static void main(String[] args) {
		System.out.println("start");

		displayQRCode();
		runServer();
		System.out.println("clicked");
	}

	public static void displayQRCode() {

		try {
			final String ip = getIp();
			if (ip != null) {

				JPanel pnlButton = new JPanel();
				JButton btnQR = new JButton("Generate QR");
				btnQR.setBounds(60, 400, 220, 30);
				pnlButton.setBounds(800, 800, 200, 100);
				pnlButton.add(btnQR);
				frame.add(pnlButton);
				frame.setSize(400, 400);
				frame.setBackground(Color.BLACK);
				frame.setTitle("Remote Mouse");
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				ActionListener listen = new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						ByteArrayOutputStream out = QRCode.from(ip)
								.to(ImageType.PNG).stream();

						try {
							qr = ImageIO.read(new ByteArrayInputStream(out
									.toByteArray()));
						} catch (IOException e) {
							e.printStackTrace();
						}

						@SuppressWarnings("serial")
						JPanel pane = new JPanel() {
							@Override
							protected void paintComponent(Graphics g) {
								super.paintComponent(g);
								g.drawImage(qr, 130, 130, null);
							}
						};
						frame.add(pane);
						frame.setVisible(true);
					}
				};
				btnQR.addActionListener(listen);
				/*
				 * FileOutputStream fout = new FileOutputStream(new
				 * File("/home/shruthi/qr.png")); fout.write(out.toByteArray());
				 * fout.flush(); fout.close();
				 */}

		} catch (Exception e) {
		}
	}

	public static void runServer() {
		try {
			System.out.println("Running server");

			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(7777);
			System.out.println("Server started. Listening on port number 7777");
			PrintWriter out;
			InputStream inputStream;
			InputStreamReader inputStreamReader;
			Socket clientSocket;
			while (true) {
				clientSocket = serverSocket.accept();
				inputStreamReader = new InputStreamReader(
						clientSocket.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);

				out = new PrintWriter(clientSocket.getOutputStream(), true);
				inputStream = new ByteArrayInputStream(bufferedReader
						.readLine().getBytes(Charset.forName("UTF-8")));
				BufferedReader bufferedReader2 = new BufferedReader(
						new InputStreamReader(inputStream));
				String output = bufferedReader2.readLine();
				System.out.println(output);

				if (output.startsWith(leftClickCode))
					MouseMovements.mouseLeftClick();
				else if (output.startsWith(rightClickCode))
					MouseMovements.mouseRightClick();
				else if (output.startsWith(doubleClickCode))
					MouseMovements.mouseDoubleClick();
				else if (output.startsWith(scrollUpCode))
					MouseMovements.mouseScroll(true);
				else if (output.startsWith(scrollDownCode))
					MouseMovements.mouseScroll(false);
				else if (output.startsWith("kyboard"))
					Keyboard.keyPress(output.replace("kyboard ", ""));
				else if (!output.startsWith(leftClickCode)
						|| !output.startsWith(rightClickCode))
					MouseMovements.mouseMove(
							Integer.parseInt(output.split(",")[0]),
							Integer.parseInt(output.split(",")[1]), 2);

				out.flush();
				inputStream.close();
				bufferedReader2.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception caught");
		}

	}

	public static String getIp() {
		String ip = null;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					ip = addr.getHostAddress();
					// System.out.println(iface.getDisplayName() + " " + ip);
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		return ip;
	}
}
