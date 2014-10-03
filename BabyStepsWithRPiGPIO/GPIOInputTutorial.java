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
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;

/* Imports for Java */
import java.util.concurrent.Callable;

/**
 *
 * @author calpilot
 */
public class GPIOInputTutorial {

	public static void main(String[] args) {
		System.out.println("========= STARTING GPIO INPUT TUTORIAL ========");
		
		/* Initialize GPIOController */
		final GpioController gpio = GpioFactory.getInstance();
		
		/*
		 * Provision GPIO_07 as an input pin. Enable its pull-down resistor.
		 */
		final GpioPinDigitalInput button = 
				gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, 
											  PinPullResistance.PULL_DOWN);
		
		/*
		 * Provision GPIO_01, GPIO_04 and GPIO_06 as output pins
		 * and initialize them to LOW.
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
		 * Add a listener that listens to a change in state of the pin.
		 * This listener prints a status message on the console and
		 * pulses led1 on every state change.
		 */
		button.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent gpdsce) {
				System.out.println("Button Listener Activated: " + 
						gpdsce.getPin() + " --> " + gpdsce.getState());
				led1.pulse(1000, false);	//false=NONBLOCKING
			}
		});
		
		/*
		 * Add a SyncStateTrigger to the button with led2. Every time the
		 * button's state changes, led2 is set to the same state as that of
		 * the button.
		 */
		button.addTrigger(new GpioSyncStateTrigger(led2));
		
		/*
		 * Add a callback trigger to the button so that a console message
		 * is printed out whenever the state of the button changes.
		 */
		button.addTrigger(new GpioCallbackTrigger(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				System.out.println("Button Callback Activated: ");
				return null;
			}
		
		}));
		
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
				gpio.shutdown();
			}
		});
		
		try {

			for (;;) {
				Thread.sleep(500);
			}
		} catch (InterruptedException ie) {
			System.out.println("\n\nPROGRAM WAS INTERRUPTED. " +
					"SHUTTING DOWN.");
		}
		
	}
	
}
