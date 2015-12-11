package com.jiangtao.tools;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 * 番茄闹钟
 *
 */
public class TomatoClock {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new TomatoClock().open(display);
		shell.setText("番茄闹钟 github.com/jiangtao520");
		final Tray tray = display.getSystemTray ();
		final TrayItem item = new TrayItem (tray, SWT.NONE);
		item.setToolTipText("番茄闹钟");
		item.setImage(new Image(display, 16, 16));
		item.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setMinimized(!shell.getMinimized());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private Timer timer = new Timer();
	private int seconds = 0;
	
	public Shell open(final Display display) {
		final Shell shell = new Shell(display, SWT.ON_TOP | SWT.CLOSE | SWT.MIN);

		GridLayout gridLayout = new GridLayout(5, false);
		shell.setLayout(gridLayout);

		Label label0 = new Label(shell, SWT.NONE);
		label0.setText("间隔");

		final Text time = new Text(shell, SWT.BORDER);
		time.setText("20");
		GridData data = new GridData(80, -1);
		time.setLayoutData(data);

		time.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				boolean isPositiveNumber = true;
				try {
					int number = Integer.parseInt(e.text);

					isPositiveNumber = number >= 0;
				} catch (Exception ex) {
					isPositiveNumber = false;
				}

				boolean isBackspace = e.keyCode == 0x8;

				e.doit = isPositiveNumber || isBackspace;
			}
		});

		time.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.ARROW_UP:
					time.setText(String.valueOf(Integer.parseInt(time.getText()) + 10));
					e.doit = false;
					break;
				case SWT.ARROW_DOWN:
					time.setText(String.valueOf(Integer.parseInt(time.getText()) - 10));
					e.doit = false;
					break;

				default:
					break;
				}
			}
		});

		Label label2 = new Label(shell, SWT.NONE);
		label2.setText("分钟");

		final Button start = new Button(shell, SWT.TOGGLE);
		start.setText("启动");
		start.setLayoutData(new GridData(80, -1));
		start.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isStart = start.getSelection();

				start.setText(isStart ? "停止" : "启动");
				
				time.setEnabled(!isStart);

				seconds = isStart ? Integer.parseInt(time.getText()) * 60 : 0;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		final Label alert = new Label(shell, SWT.NONE);
		alert.setLayoutData(new GridData(150, -1));
		
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (seconds > 0){
					seconds--;
					
					if (seconds == 0){
						display.syncExec(new Runnable() {
							public void run() {
								if (shell.getMinimized()) {
									shell.setMinimized(false);
								}
								TomatoClock.notify(shell);
								
								start.setSelection(false);
								start.setText("启动");
								time.setEnabled(true);
								
							}
						});
					}
					
					final String msg = seconds == 0 ? "时间到" : "剩余: " + (seconds / 60) + "分"+ (seconds %60) + "秒";
					display.syncExec(new Runnable() {
						public void run() {
							alert.setText(msg);
						}
					});
				}
			}
		}, 0, 1000);
		
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				timer.cancel();
			}
		});

		shell.pack();
		shell.open();
		
		return shell;
	}
	
	private static  void notify(Shell shell) {
		int num = 120;// 抖动次数
		Point point = shell.getLocation();// 窗体位置
		for (int i = 20; i > 0; i--) {
			for (int j = num; j > 0; j--) {
				point.y += i;
				shell.setLocation(point);
				point.x += i;
				shell.setLocation(point);
				point.y -= i;
				shell.setLocation(point);
				point.x -= i;
				shell.setLocation(point);
			}
			shell.getDisplay().beep();
		}
	}
}
