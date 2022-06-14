package com.ballgame

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.nio.file.Paths.get
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip


class AudioManager {
	private val audioPath = "kotlin/com/ballgame/assets/audio"
	val introMusic: Clip?
	val gameMusic: Clip?
	val loseMusic: Clip?
	val fireballContactEffect: Clip?

	init {
		// Music From https://downloads.khinsider.com/
		introMusic = getMediaPlayer("intro.wav")
		gameMusic = getMediaPlayer("game.wav")
		loseMusic = getMediaPlayer("lose.wav")
		fireballContactEffect = getMediaPlayer("fireball-contact.wav")
	}

	/**
	 * @param path path of audio file located in [AudioManager.audioPath]
	 * @return MediaPlayer JavaFX Object
	 */
	private fun getMediaPlayer(path: String) = try {
		AudioSystem.getClip().also {
			it.open(AudioSystem.getAudioInputStream(BufferedInputStream(FileInputStream(get(audioPath, path).toFile()))))
		}
	} catch (e: Exception) {
		e.printStackTrace()
		null
	}
}
