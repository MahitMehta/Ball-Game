package com.ballgame

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.file.Paths.get
import java.util.*
import javax.imageio.ImageIO.read


class AssetManager {
	private val assetsPath = "kotlin/com/ballgame/assets/"
	private val fireballSpritesPaths = arrayOf(
		"fireball/fb-1.png", "fireball/fb-2.png", "fireball/fb-3.png", "fireball/fb-4.png", "fireball/fb-5.png"
	)
	private val heartSpritesPaths = arrayOf(
		"static/heart.png"
	)
	val fireballSprites: ArrayList<BufferedImage> = ArrayList()
	val heartSprites: ArrayList<BufferedImage> = ArrayList()

	init {
		try {
			loadAssets(fireballSprites, fireballSpritesPaths);
			loadAssets(heartSprites, heartSpritesPaths);
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	@Throws(IOException::class)
	fun loadAssets(sprites:MutableList<BufferedImage>, spritePaths:Array<String>) {
		spritePaths.mapTo(sprites) { read(File(get(assetsPath, it).toUri())) }
	}

	companion object {
		fun <T> clone(generic: Class<T>?, e: List<T>) = LinkedList(e)
	}
}
