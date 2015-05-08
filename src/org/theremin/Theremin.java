package org.theremin;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.*;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.System.*;
import static java.lang.Math.*;
public class Theremin implements ChangeListener, ActionListener, Runnable {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private static final boolean PLAY_SONG = false;
	private static final int HARMONIC_MULTIPLIERS_TO_USE = 2;
	private static final boolean LOCK_TONE_TO_NOTES = true;

	private static final int sampleRate = 100000;
	private static final double expectedAmplitude = .5;
	private static final double[] harmonicContent = new double[]{1, 2, 3, 4, 5, 6, 7, 8};
	private static final double[][] harmonicMultiplierSet = new double[][]{
			new double[]{1, .9, .8, .7, .6, .5, .4, .3},
			//flute
			new double[]{1, 1, .1, .2, .15, .01, .01, .01},
			//saxophone
			new double[]{.52, 1, .04, .04, .01, 0, 0, 0},
			//harpsichord
			new double[]{1, .8, .6, .4, .4, .6, .8, 1},
			new double[]{1, .7, .7, .8, .5, .2, .5, .1},
			new double[]{1, .1, .9, .1, .2, .3, .05, .1},
			new double[]{1, 0, .8, 0, .6, 0, .4, 0},
			new double[]{.1, .3, .4, .5, .6, .7, .9, 1}
	};
	private static final double[] harmonicMultipliers = harmonicMultiplierSet[HARMONIC_MULTIPLIERS_TO_USE];
	private static final double echoFade = .7;
	private static final Waveform waveform = Waveform.SAWTOOTH;
	private static final double echoSamples = .06 * sampleRate;
	private static double[] notes;

	private double expectedFrequency = 2000;
	private double frequency = 2000;
	private double amplitude = 1;
	private Queue<Double> echo = new ArrayDeque<>();
	{
		for(int x=0;x<echoSamples; x++){
			echo.add(0.0);
		}
	}
	private JSlider slider;
	private JTextField textbox, amptextbox;
	private JButton button;

	public static void main(String[] args) throws Exception {
		new Theremin();
	}
	public Theremin() throws Exception {
		notes = new double[85];
		double freq = 0;
		for (int n = -35; freq < 7000 && n + 36 < 85; n++) {
			freq = 440 * pow(2, n / 12.0);
			notes[n + 36] = freq;
		}
		out.println(Arrays.toString(notes));

		JFrame frame = new JFrame();
		JPanel panel = new JPanel();

		slider = new JSlider(SwingConstants.HORIZONTAL, 0, 500000, 44000);
		textbox = new JTextField("440.00");
		amptextbox = new JTextField("1.000");
		button = new JButton("Enter");
		slider.addChangeListener(this);
		button.addActionListener(this);
		panel.add(amptextbox);
		panel.add(slider);
		panel.add(textbox);
		panel.add(button);
		panel.setSize(1100, 200);
		frame.add(panel);
		frame.setSize(1100, 200);
		textbox.setPreferredSize(new Dimension(100, 40));
		amptextbox.setPreferredSize(new Dimension(100, 40));
		slider.setPreferredSize(new Dimension(800, 200));
		slider.setLocation(0, 0);
		// frame.pack();
		frame.setVisible(true);
		Thread thread1 = new Thread(this);
		thread1.start();
		playSound();
	}
	public int[] song = new int[]{
			46, 43, 39, 34, 36, 38, 39, 36, 39, 34,
			41, 46, 43, 39, 36, 38, 39, 41, 43, 41, 43,
			44, 43, 41, 46, 43, 41, 39, 41, 43, 39, 36, 39, 36, 34, 34,
			39, 43, 41, 34, 39, 43, 41, 43, 44, 46, 43, 39, 41, 34, 39};
	public double[] songtimes = new double[]{
			3, 3, 3, 3, 1, 1, 1, 2, 1, 6,
			3, 3, 3, 3, 1, 1, 1, 2, 1, 6, 1,
			1, 1, 1, 2, 1, 1, 4, 1, 2, 1, 2, 1, 1, 4, 1,
			2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 6};
	@Override
	public void run() {
		try {
			Controller controller = new Controller();
			new org.signlanguage.LeapListener(controller);
			assert (controller != null);

			while (controller == null) {
				Thread.sleep(100);
			}

			int octaveOffset = 0;
			if(PLAY_SONG){
				for (int x = 0; x < song.length; x++) {
					out.println(notes[song[x]+12 * octaveOffset]);
					out.println("hello");
					setFrequency(notes[song[x]+12 * octaveOffset]);
					setAmplitude(1);
					Thread.sleep((int) (150 * songtimes[x]));
				}
			}
			while (true) {
				Frame curr = controller.frame();
				if (!curr.hands().isEmpty()) {
					Hand currenthand = curr.hands().get(0);
					for (Hand temp : curr.hands()) {
						if (temp.isRight())
							currenthand = temp;
					}
					if (currenthand != null) {
						setFrequency(min(notes[84],max(notes[1],
								2000 * sqrt(max(0, currenthand.palmPosition().getY() - 60)) * currenthand.palmPosition().getY() / (sqrt(500) * 500)
								)));
						Finger index = null;
						Finger thumb = null;
						for (Finger f : currenthand.fingers()) {
							if (f.type() == Finger.Type.TYPE_INDEX)
								index = f;
							if (f.type() == Finger.Type.TYPE_THUMB)
								thumb = f;
						}
						double dist = (index.tipPosition().distanceTo(thumb
								.tipPosition()));
						dist = (currenthand.palmPosition().getX());
						setAmplitude((dist + 10) / 50);
					} else {
						setFrequency(0);
					}
				} else {
					setFrequency(0);
				}
				Thread.sleep(5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// setFrequency(slider.getValue() / 100, slider);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// setFrequency((int) (Double.parseDouble(textbox.getText())), textbox);

	}
	public void setAmplitude(double in) {
		in = min(1, max(0, in));
		if (amptextbox != null)
			amptextbox.setText(String.format("%.3f", in));
		amplitude = in;
	}
	public void setFrequency(double in) {
		setFrequency(in, "");
	}
	public void setFrequency(double in, Object parent) {
		if(LOCK_TONE_TO_NOTES){
			int best = abs(Arrays.binarySearch(notes, in) + 1);
			//out.println(best);
			if (best > 0 && best < 85 && abs(in - notes[best - 1]) < abs(in - notes[best])) {
				best--;
			}
			//out.println(best);
			in = notes[max(0, best)];
		}
		expectedFrequency = in;
		//out.println(in);
		if (textbox != null && parent != textbox)
			textbox.setText(String.format("%.2f", in));
		// in = 5000*log(in-60)/log(5000)+60;
		if (slider != null && parent != slider)
			slider.setValue((int) (100 * in));
	}
	void playSound() throws LineUnavailableException {
		out.println(Arrays.toString(AudioSystem
				.getTargetLineInfo(new Line.Info(SourceDataLine.class))));
		out.println(AudioSystem.getMixer(null).getLine(
				(AudioSystem.getMixer(null).getSourceLineInfo()[0])));
		AudioFormat audioFormat = new AudioFormat(sampleRate, 16, 1, true,
				false);
		out.println(audioFormat);
		SourceDataLine soundLine = AudioSystem.getSourceDataLine(audioFormat);
		soundLine.open(audioFormat);

		double[] wavepositions = new double[harmonicContent.length];
		double amplitudeFactor = 0;
		for (int x = 0; x < harmonicContent.length; x++) {
			amplitudeFactor += harmonicContent[x] * harmonicMultipliers[x];
		}
		soundLine.start();
		for (int count = 0; true; count++) {
			if(count % 2000 == 0)
				setAmplitude(amplitude);
			frequency += .005 * (expectedFrequency - frequency);
			//if (expectedFrequency < 1)
			//	soundLine.flush();
			amplitude = amplitude + .00005 * (expectedAmplitude - amplitude);

			double diff = 0;
			for (int x = 0; x < wavepositions.length; x++) {
				wavepositions[x] += frequency * harmonicContent[x] / sampleRate;
				wavepositions[x] %= 1;
				if (frequency == 0 || expectedFrequency == 0 || amplitude == 0)
					wavepositions[x] = 0;

				double tempDiff = 0;
				if (waveform == Waveform.SINE)
					tempDiff = sin(wavepositions[x] * 4 * PI);
				else if (waveform == Waveform.SAWTOOTH)
					tempDiff = 1 - 2* wavepositions[x];
				else if (waveform == Waveform.SQUARE)
					tempDiff = wavepositions[x] > .5 ? -1 : 1;
				else if (waveform == Waveform.TRIANGLE)
					tempDiff = abs(wavepositions[x] * 4 - 2) - 1;

				diff += tempDiff * harmonicMultipliers[x];
			}
			double gain = amplitude * Short.MAX_VALUE
					/ (amplitudeFactor * log(expectedFrequency));
			diff *= gain;

			if(echoSamples != 0 && echoFade != 0){
				diff += echoFade * echo.remove();
				echo.add(diff);
			}

			//out.println(diff);
			byte[] data = intToByteArray((int) round(diff));
			soundLine.write(data, 0, 4);
			while (soundLine.getBufferSize() - soundLine.available() > 5000) {
				try {
					Thread.sleep(0, 5);
				} catch (Exception e) {
				};
			}
		}

	}
	public static byte[] intToByteArray(int value) {
		return new byte[]{(byte) value, (byte) (value >>> 8),
				(byte) (value >>> 16), (byte) (value >>> 24)};
	}
	public static byte[] shortToByteArray(short value) {
		return new byte[]{(byte) value, (byte) (value >>> 8)};
	}
	public enum Waveform {
		SINE, SAWTOOTH, SQUARE, TRIANGLE
	}
}