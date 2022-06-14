package com.ballgame

import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.Ellipse2D
import java.awt.geom.Ellipse2D.*
import java.awt.image.BufferedImage
import kotlin.Double
import kotlin.math.atan

class Fireball(
	x: Int, y: Int, velX: Double, velY: Double, r: Int, sprites: List<BufferedImage>
) : Circle(x, y) {
	private val shape: Shape
	var velX: Double
	var velY: Double

	init {
		initSprites(sprites, 15, 1.6)
		this.velX = velX
		this.velY = velY
		shape = Double(this.x, this.y, diameter.toDouble(), diameter.toDouble())
	}

	fun getShape() = shape as Ellipse2D.Double

	fun rotateSprites() = sprites!!.indices.forEach {
		transformedSprites[it] = rotateImage(sprites!![it], atan(velX / -velY) * 180)
	}

	override fun update(deltaTime: Double, previousTime: Double) {
//		if (incrementImageFrame(deltaTime, previousTime)) image = rotateImage(image!!, atan(velX / -velY) * 180)
		super.setTransform(
			x + (velX * (deltaTime - previousTime)), y + (velY * (deltaTime - previousTime))
		)
	}

	override fun render(g2d: Graphics2D?) {
		val diameter = (diameter * imagePaddingFactor).toInt()
		val translationAdjustment = (diameter - this.diameter) / 2
		g2d!!.drawImage(
			image, x.toInt() - translationAdjustment, y.toInt() - translationAdjustment, diameter, diameter, null
		)
		(shape as Ellipse2D.Double).setFrame(
			x, y, this.diameter.toDouble(), this.diameter.toDouble()
		)
	}
}
