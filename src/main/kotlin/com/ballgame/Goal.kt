package com.ballgame

import java.awt.Color.GREEN
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.Ellipse2D

class Goal @JvmOverloads constructor(x: Int = 0, y: Int = 0, var r: Int = 30) : Circle(x, y) {
	private val shape: Shape

	init {
		shape = Ellipse2D.Double(
			this.x, this.y, diameter.toDouble(), diameter.toDouble()
		)
	}

	fun getShape() = shape as Ellipse2D.Double

	override fun update(deltaTime: Double, previousTime: Double) {}
	override fun render(g2d: Graphics2D?) {
		g2d!!.color = GREEN
		(shape as Ellipse2D.Double).setFrame(
			x, y, diameter.toDouble(), diameter.toDouble()
		)
		g2d.fill(shape)
	}
}
