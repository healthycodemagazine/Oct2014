/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Charathram Ranganathan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package org.calpilot;

/* Required imports for Pi4J */
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Created by calpilot on 5/14/2014.
 */
public class Tutorial2 {
	public static void main(String[] args) {

		boolean BLOCKING = true;
		boolean NONBLOCKING = false;

		System.out.println("===== Starting Tutorial 2 =====");

		final GpioController gpio = GpioFactory.getInstance();

		/*
		 * Initialize all the pins as outputs, and pull them to a LOW on
		 * startup.
		 */
		final GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_01,
				"LED1",
				PinState.LOW);

		final GpioPinDigitalOutput led2 = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_04,
				"LED2",
				PinState.LOW);

		final GpioPinDigitalOutput led3 = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_06,
				"LED3",
				PinState.LOW);

		/*
		 * Add a shutdown hook so that the application can trap a Ctrl-C 
		 * and handle it gracefully by ensuring that all the LEDs are 
		 * turned off prior to exiting.
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("\n\nPROGRAM WAS INTERRUPTED. " +
					"SHUTTING DOWN!");
				led1.low();
				led2.low();
				led3.low();
				gpio.shutdown();
			}
		});

		System.out.println("Pins initialized. All LEDs should be off.");

		/*
		 * Starting the main loop of our program. We put it in a 
		 * try-catch since the various sleep's throw an exception and 
		 * we need to catch those exceptions.
		 */
		try {
			System.out.println("Blocking Call: Each LED will be on " +
					"for 1 sec one after another.");
			Thread.sleep(1000);

			/*
			 * Blocking call: Execution proceeds to next line only 
			 * after current line is finished. LEDs will blink one
			 * after the other.
			 */
			led1.pulse(1000, BLOCKING);
			led2.pulse(1000, BLOCKING);
			led3.pulse(1000, BLOCKING);

			System.out.println("Non-Blocking Call: Each LED will be " +
					"on for 1 sec, but simultaneously.");
			Thread.sleep(1000);

			/*
			 * Non-blocking call: Each LED should turn on and off
			 * simultaneously. Execution does not wait for a 
			 * statement to finish before moving to next statement.
			 */
			led1.pulse(1000, NONBLOCKING);
			led2.pulse(1000, NONBLOCKING);
			led3.pulse(1000, NONBLOCKING);

			System.out.println("Blinking LED2 for 10 seconds, " +
					"every second.");
			Thread.sleep(1000);

			/*
			 * Blink LED2 for a total of 10 seconds, with each cycle
			 * being 1 sec long. LED2 will be on for 500 ms and off 
			 * for 500 ms.
			 */
			led2.blink(500, 10000);

			System.out.println("Blinking LED3 forever, every 2 " +
					"seconds. Press Ctrl-C to abort the program.");

			/*
			 * Another way to blink a GPIO pin is to set it high and 
			 * low alternately, with a sleep. This is inefficient.
			 */
			for (; ; ) {
				led3.high();
				System.out.print(".");
				Thread.sleep(1000);
				led3.low();
				System.out.print(" ");
				Thread.sleep(1000);
			}
		} catch (InterruptedException ie) {
			System.out.println("Execution Interrupted.");
			led1.low();
			led2.low();
			led3.low();
			gpio.shutdown();
		}
	}
}
