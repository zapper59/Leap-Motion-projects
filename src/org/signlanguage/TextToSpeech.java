package org.signlanguage;

import java.util.*;
import java.io.*;

import javax.tools.JavaFileObject;

import static java.lang.System.*;
import de.dfki.lt.freetts.en.us.*;
import de.dfki.lt.freetts.mbrola.*;

import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.Voice;

import java.util.*;

import static java.lang.System.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import us.monoid.web.BinaryResource;
import us.monoid.web.Resty;

public class TextToSpeech {

	private static final String BASE_URL = "http://translate.google.com/translate_tts?ie=UTF-8&q={0}&tl={1}&prev=input";

	public static void main(String[] args) {
		try {
			sayToGoogle("azul");
			sayToGoogle("The quick brown fox took a jump");
		} catch (Exception e) {
		}
	}
	public static void speak(String s) throws FileNotFoundException {


		try {
			VoiceManager v = VoiceManager.getInstance();
			Voice voice = v.getVoice("kevin16");
			voice.allocate();
			voice.speak(s);

		} catch (Exception e) {
		}
	}
	public static void sayToGoogle(String text) throws FileNotFoundException {
		try {

			File f = new File("translate.mp3");
			f.createNewFile();
			String temp = "";
			for (char a : text.toCharArray()) {
				if (("" + a).matches("[0-9]"))
					temp += a + " ";
				else
					temp += a;
			}
			text = temp;

			speak(text);
		} catch (Exception e){
			speak(text);
		}


	}
}
