package me.nicba1010.pluginmanager;

import static me.nicba1010.pluginmanager.Commons.*;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer implements Runnable {
	long			time		= 0;
	boolean			running		= false;
	Runnable		task;
	long			timemax		= 20000;
	int				optional	= 0;
	boolean			pause		= false;
	CommandSender	sender		= null;

	public Timer(CommandSender sender) {
		this.sender = sender;
	}

	@Override
	public void run() {
		while (true) {
			if (running && !pause) {
				if (time > timemax)
					stop();
				long start = System.currentTimeMillis();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long stop = System.currentTimeMillis();
				time += stop - start;
			} else
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}

	public void assignTask(Runnable runnable) {
		task = runnable;
	}

	public void stop() {
		running = false;
		if (time > timemax)
			serverMessage("The time has run out!");
		new Thread(new BukkitRunnable() {

			@Override
			public void run() {
				Main.getPlayerData().timerMap.remove(sender);
			}
		}).start();
	}

	public void pause() {
		pause = true;
	}

	public void unpause() {
		pause = false;
	}

	public void specialTime(long a) {
		timemax = a;
	}

	public void doTask() {
		if (running) {
			pause();
			task.run();
			stop();
		}
	}

	public void reset() {
		serverMessage("Enter /pm yes OR /pm no!\nYou have " + timemax / 1000 + " seconds!");
		time = 0;
	}

	public void start(String message) {
		new Thread(this).start();
		running = true;
		serverMessage(message + "\nYou have " + timemax / 1000 + " seconds!");
	}

}
