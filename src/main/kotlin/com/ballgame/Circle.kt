package com.ballgame

abstract class Circle(x: Int, y: Int) : GameObject(x, y) {
	var radius = 0
		protected set
	private val centerX: Double
		get() = this.x + radius
	val diameter: Int
		get() = radius * 2
	private val centerY: Double
		get() = this.y + radius

	fun intersects(other: Circle) = this.getDistance(
		centerX, centerY, other.centerX, other.centerY
	) <= radius + other.radius
}
