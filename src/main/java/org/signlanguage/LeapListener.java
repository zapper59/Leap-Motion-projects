package org.signlanguage;

import com.leapmotion.leap.*;
public class LeapListener implements Runnable {
	private Controller current;
	public LeapListener(Controller parent) {
		current = parent;
		new Thread(this).start();
	}
	private int backcount = 0, entercount = 0;
	public void onFrame() {
		Frame curr = current.frame();
		boolean backgesture = false, entergesture = false, fastback = false;
		for (Gesture a : curr.gestures()) {
			if (a.type() == Gesture.Type.TYPE_CIRCLE) {
				CircleGesture circle = new CircleGesture(a);
				if (circle.radius() < 20 && circle.radius() > 7 && backcount > 8)
					fastback = true;
				if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 4) {
					entergesture = true;

				} else {
					backgesture = true;
				}
			}
		}
		if (entergesture) {
			entercount++;
			if (entercount > 20) {
				entercount = 0;
				Leap.enter();
			}
			backcount = 0;
		} else
			entercount = 0;
		if (backgesture) {
			// out.println()
			entercount = 0;
			backcount++;
			if (fastback)
				backcount += 3;
			if (backcount > 35) {
				backcount = 0;
				Leap.backspace();
			}
		} else
			backcount = 0;
	}
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			onFrame();
		}
	}
}
