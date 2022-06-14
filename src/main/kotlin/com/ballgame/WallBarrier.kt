package com.ballgame

import com.ballgame.Eskiv.Companion.HEIGHT
import com.ballgame.Eskiv.Companion.WIDTH
import com.ballgame.WallBarrier.Companion.Side.LEFT
import com.ballgame.WallBarrier.Companion.Side.RIGHT
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.Rectangle2D
import java.awt.geom.Rectangle2D.*
import kotlin.Double

class WallBarrier(width: Int, height: Int, side: Side) : Rectangle(0, 0) {
	val shape: Shape
	private var velX: Double
	var velY: Double
	val side: Side

	init {
		if (side == LEFT) x = 150.0 else if (side == RIGHT) x = (WIDTH - width - 150).toDouble()
		this.side = side
		this.width = width
		this.height = height
		velY = 0.0
		velX = 0.0
		shape = Double(x, y, this.width.toDouble(), this.height.toDouble())
	}

	override fun update(deltaTime: Double, previousTime: Double) {
		if (((side == LEFT) || (side == RIGHT)) && ((y + height) >= HEIGHT) && (velY > 0)) velY *= -1.0 else if ((side == LEFT || side == RIGHT) && y <= 0 && velY < 0) velY *= -1.0
		super.setTransform(x + (velX * (deltaTime - previousTime)), y + (velY * (deltaTime - previousTime)))
	}

	override fun render(g2d: Graphics2D?) {
		val obj = shape as Rectangle2D.Double
		obj.setFrame(x, y, width.toDouble(), height.toDouble())
		g2d!!.fill(shape)
	}

	companion object {
		enum class Side {
			LEFT, RIGHT
		}
	}
}
