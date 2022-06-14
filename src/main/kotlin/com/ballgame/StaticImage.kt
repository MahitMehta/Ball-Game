package com.ballgame

import java.awt.Graphics2D
import java.awt.image.BufferedImage

class StaticImage(x: Int, y: Int, val width: Int, val height: Int, sprites: ArrayList<BufferedImage>) :
	GameObject(x, y) {

	override fun update(deltaTime: Double, previousTime: Double) {
		incrementImageFrame(deltaTime, previousTime)
	}

	override fun render(g2d: Graphics2D?) {
		g2d!!.drawImage(image, x.toInt(), y.toInt(), width, height, null)
	}

	init {
		initSprites(sprites, 1, 0.0)
	}
}
