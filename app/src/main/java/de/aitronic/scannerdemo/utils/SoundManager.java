package de.aitronic.scannerdemo.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

import de.aitronic.scannerdemo.R;


public class SoundManager {
	private static SoundManager single = null;
	
	HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
	private SoundPool soundPool;
	private float volumnRatio;
	private AudioManager am;
	
	
	
	private SoundManager(Context context)
	{
		soundPool = new SoundPool(10, AudioManager.STREAM_RING, 5);
		soundMap.put(1, soundPool.load(context, R.raw.barcodebeep, 1));
		soundMap.put(2, soundPool.load(context, R.raw.serror, 1));
		am = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
	}

	public synchronized static SoundManager getInstance(Context context) {
		if (single == null) {
			single = new SoundManager(context);
		}
		return single;

	}
	

	public void playSound(int id) {

		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_RING);
		float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_RING);
		volumnRatio = audioCurrentVolumn / audioMaxVolumn;

		try {
			soundPool.play(soundMap.get(id), volumnRatio,
					volumnRatio,
					1,
					0,
					1
					);
		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	
	
}
