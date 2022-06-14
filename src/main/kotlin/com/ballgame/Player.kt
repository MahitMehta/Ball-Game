package com.ballgame

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.Ellipse2D
import java.awt.geom.Ellipse2D.*
import kotlin.Double
import kotlin.math.abs

class Player @JvmOverloads constructor(
	x: Int = 0, y: Int = 0, private val speed: Double = 120.0
) : Circle(x, y) {
	var velX = 0.0
	private var velY = 0.0
	private val a = 500.0
	private val da = 900.0
	val shape: Shape
		get() = field as Ellipse2D.Double
	var left = false
	var down = false
	var up = false
	var right = false

	init {
		this.radius = 16
		shape = Double(this.x, this.y, diameter.toDouble(), diameter.toDouble())
	}

	override fun update(deltaTime: Double, previousTime: Double) {
		val incrementX = velX * (deltaTime - previousTime)
		val incrementY = velY * (deltaTime - previousTime)
		val velIncrement = a * (deltaTime - previousTime)
		val velDIncrement = da * (deltaTime - previousTime)
		if (right && ((velX + velIncrement) < speed)) velX += velIncrement else if (!left && velX >= 0) if (velX - velDIncrement >= 0) velX -= velDIncrement else velX =
			0.0
		if (this.down && ((velY + velIncrement) < speed)) velY += velIncrement else if (!up && velY >= 0) if (velY - velDIncrement >= 0) velY -= velDIncrement else velY =
			0.0
		if (this.up && (abs(velY - velIncrement) < speed)) velY -= velIncrement else if (!down && velY <= 0) if (velY + velDIncrement < 0) velY += velDIncrement else velY =
			0.0
		if (this.left && (abs(velX - velIncrement) < speed)) velX -= velIncrement else if (!right && velX <= 0) if (velX + velDIncrement < 0) velX += velDIncrement else velX =
			0.0
		if ((up && (velY > 0)) || (down && (velY < 0))) velY *= -1
		if ((right && (velX < 0)) || (left && (velX > 0))) velX *= -1
		setTransform(x + incrementX, y + incrementY)
	}

	override fun render(g2d: Graphics2D?) {
		g2d!!.color = Color.BLUE
		val obj = shape as Ellipse2D.Double
		obj.setFrame(x, y, diameter.toDouble(), diameter.toDouble())
		g2d.fill(shape)
	}
}
