package com.ballgame

import com.ballgame.AssetManager.Companion.clone
import java.awt.Graphics2D
import java.awt.Image
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import kotlin.math.*

abstract class GameObject @JvmOverloads constructor(x: Int = 0, y: Int = 0) {
	var x: Double
		protected set
	var y: Double
		protected set
	protected var imagePaddingFactor = 1.0
	protected var image: BufferedImage? = null
	var imageFrame = 0.0
	private var imageFPS = 0
	var sprites: List<BufferedImage>? = null
		protected set
	protected lateinit var transformedSprites: MutableList<BufferedImage>

	init {
		this.x = x.toDouble()
		this.y = y.toDouble()
	}

	protected fun initSprites(sprites: List<BufferedImage>, imageFPS: Int = 24, imagePaddingFactor: Double = 1.0) {
		if (sprites.isNotEmpty()) {
			transformedSprites = clone(BufferedImage::class.java, sprites)
			this.imagePaddingFactor = imagePaddingFactor
			this.sprites = sprites
			this.imageFPS = imageFPS
			imageFrame = 0.0
			image = this.sprites!![imageFrame.toInt()]
		}
	}

	protected fun incrementImageFrame(deltaTime: Double, previousTime: Double): Boolean {
		val prevFrame = imageFrame.toInt()
		imageFrame =
			(if (imageFrame.toInt() < (sprites!!.size - 1)) imageFrame + (imageFPS * (deltaTime - previousTime)) else 0.0)
		if (prevFrame == imageFrame.toInt()) return false
		image = sprites!![imageFrame.toInt()]
		return true
	}

	fun getDistance(x: Double, y: Double, otherX: Double, otherY: Double) =
		sqrt((otherX - x).pow(2) + (otherY - y).pow(2))

	fun rotateImage(image: Image, angle: Double): BufferedImage {
		val rads = Math.toRadians(angle)
		val sin = abs(sin(rads))
		val cos = abs(cos(rads))
		val w = image.getWidth(null)
		val h = image.getHeight(null)
		val newWidth = floor((w * cos) + (h * sin * (1 / imagePaddingFactor))).toInt()
		val newHeight = floor((h * cos) + (w * sin * (1 / imagePaddingFactor))).toInt()
		val rotated = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
		val g2d = rotated.createGraphics()
		val at = AffineTransform()
		at.translate(((newWidth - w) / 2).toDouble(), ((newHeight - h) / 2).toDouble())
		val x = w / 2
		val y = h / 2
		at.rotate(rads, x.toDouble(), y.toDouble())
		g2d.transform = at
		g2d.drawImage(image as BufferedImage, 0, 0, null)
		g2d.dispose()
		return rotated
	}

	fun setTransform(x: Double, y: Double) {
		this.x = x
		this.y = y
	}

	fun setTransformX(x: Double): Double {
		this.x = x
		return this.x
	}

	fun setTransformY(y: Double): Double {
		this.y = y
		return this.y
	}

	abstract fun update(deltaTime: Double, previousTime: Double)
	abstract fun render(g2d: Graphics2D?)
}
